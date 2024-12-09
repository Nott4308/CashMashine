package org.example;

import java.util.*;

public class ATM {
    private final Scanner scanner = new Scanner(System.in);
    private final HashMap<Money, Integer> dengi = new HashMap<>();
    public ATM() {
        dengi.put(Money.grn500, 20);
        dengi.put(Money.grn200, 20);
        dengi.put(Money.grn100, 30);
        dengi.put(Money.grn50, 30);
        dengi.put(Money.grn20, 40);
        dengi.put(Money.grn10, 40);
        dengi.put(Money.grn5, 40);
    }

    private HashMap<String, Integer> passwordAcc(){
        HashMap<String, Integer> account = new HashMap<>();
        account.put("Admin", 1111);
        account.put("User", 2222);
        return account;
    }

    private HashMap<Integer, Integer> callUserBalance(){
        HashMap<Integer, Integer> accountBalance = new HashMap<>();
        accountBalance.put(passwordAcc().get("User"), 5000);
        return accountBalance;
    }

    public void start() {
        while (true) {
            System.out.println("Увійдіть до аккаунта: \n1. Admin \n2. User \n3. Вихід");
            int choice = setScanner();
            switch (choice) {
                case 1:
                    authorizationAccAdmin();
                    break;
                case 2:
                    authorizationAccUser();
                    break;
                case 3:
                    System.out.println("Вихід");
                    return;
                default:
                    System.out.println("Невірний вибір");
            }
        }
    }

    public void authorizationAccAdmin(){
        while (true){
            System.out.print("Введи пароль: ");
            int password = setScanner();
                if(password == passwordAcc().get("Admin")){
                    callAdminMenu();
                    return;
                } else System.out.println("Невірний пароль");}
    }

    public void authorizationAccUser(){
        while (true){
            System.out.print("Введи пароль: ");
            int password = setScanner();
            if(password == passwordAcc().get("User")){
                callAccUser();
                return;
            } else System.out.println("Невірний пароль");}
    }

    public void callAdminMenu() {
        while (true) {
            System.out.println("\n1. Перевірка залишку купюр \n2. Поповнення \n3. Вихід");
            int choice = setScanner();
            switch (choice) {
                case 1:
                    checkingBills();
                    break;
                case 2:
                    replenishment();
                    break;
                case 3:
                    System.out.println("Вихід з облікового запису Admin");
                    return;
                default:
                    System.out.println("Невірний вибір");
            }
        }
    }

    public void checkingBills(){
        System.out.println("500грн " + dengi.get(Money.grn500));
        System.out.println("200грн " + dengi.get(Money.grn200));
        System.out.println("100грн " + dengi.get(Money.grn100));
        System.out.println("50грн " + dengi.get(Money.grn50));
        System.out.println("20грн " + dengi.get(Money.grn20));
        System.out.println("10грн " + dengi.get(Money.grn10));
        System.out.println("10грн " + dengi.get(Money.grn5));
    }

    public void replenishment() {
        System.out.print("Введіть номінал: ");
        int nominalValue = setScanner();
        Money moneyNominal = Money.fromValue(nominalValue);
        if (moneyNominal == null || !dengi.containsKey(moneyNominal)) {
            System.out.println("Невірний номінал. Поповнення неможливе.");
            return;
        }

        System.out.print("Введіть кількість купюр: ");
        int count = setScanner();
        if (count <= 0) {
            System.out.println("Кількість повинна бути більше нуля.");
            return;
        }

        int currentCount = dengi.getOrDefault(moneyNominal, 0);
        dengi.put(moneyNominal, currentCount + count);

        System.out.println("Поповнення успішне!");
        System.out.println("Новий стан банкомату:");
        dengi.forEach((key, value) ->
                System.out.println(key.getValue() + " грн: " + value + " шт.")
        );
    }

    public void callAccUser() {
        while (true) {
            System.out.println("\n1. Баланс \n2. Зняття \n3. Вихід");
            int choice = setScanner();
            switch (choice) {
                case 1:
                    balance();
                    break;
                case 2:
                    withdrawMoney();
                    break;
                case 3:
                    System.out.println("Вихід з облікового запису User");
                    return;
                default:
                    System.out.println("Невірний вибір");
            }
        }
    }

    public void balance(){
        Integer userBalance = callUserBalance().get(passwordAcc().get("User"));
        System.out.println("Ваш баланс: " + userBalance + "грн");
    }

    public void withdrawMoney() {
        System.out.print("Введіть суму для зняття: ");
        int amountToWithdraw = setScanner();

        if (amountToWithdraw <= 0) {
            System.out.println("Сума має бути більше 0.");
            return;
        }

        int currentUserBalance = callUserBalance().get(passwordAcc().get("User"));
        if (amountToWithdraw > currentUserBalance) {
            System.out.println("Недостатньо коштів на рахунку.");
            return;
        }

        Result result = getResult(amountToWithdraw);

        if (result.remainingAmount() > 0) {
            System.out.println("Неможливо видати зазначену суму доступними номіналами, сума повинна закінчувати на 0 або 5");
            return;
        }

        currentUserBalance -= amountToWithdraw;
        callUserBalance().put(passwordAcc().get("User"), currentUserBalance);

        extracted(result.cashOutput());

        System.out.println("Видано кошти:");
        result.cashOutput().forEach((key, value) ->
                System.out.println(key.getValue() + " грн: " + value + " шт.")
        );

        System.out.println("Ваш новий баланс: " + currentUserBalance + " грн");
    }

    private Result getResult(int amountToWithdraw) {
        HashMap<Money, Integer> cashOutput = new HashMap<>();
        int remainingAmount = amountToWithdraw;

        List<Money> sortedNominals = new ArrayList<>(dengi.keySet());
        sortedNominals.sort(Collections.reverseOrder(Comparator.comparingInt(Money::getValue)));

        for (Money denom : sortedNominals) {
            int denomValue = denom.getValue();
            int availableCount = dengi.getOrDefault(denom, 0);

            if (remainingAmount >= denomValue && availableCount > 0) {
                int countNeeded = remainingAmount / denomValue;
                int countToGive = Math.min(countNeeded, availableCount);

                cashOutput.put(denom, countToGive);
                remainingAmount -= countToGive * denomValue;
            }
        }
        return new Result(cashOutput, remainingAmount);
    }

    private record Result(HashMap<Money, Integer> cashOutput, int remainingAmount) {
    }

    private void extracted(HashMap<Money, Integer> cashOutput) {
        for (Map.Entry<Money, Integer> entry : cashOutput.entrySet()) {
            Money denom = entry.getKey();
            int countToGive = entry.getValue();
            dengi.put(denom, dengi.get(denom) - countToGive);
        }
    }

    private int setScanner(){
        return scanner.nextInt();
    }

}
