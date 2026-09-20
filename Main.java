import java.util.Scanner;

class Road {
    String name;
    int vehicles;
    boolean emergency;

    Road(String name) {
        this.name = name;
        vehicles = 0;
        emergency = false;
    }
}

public class Main {

    static Road north = new Road("North");
    static Road south = new Road("South");
    static Road east = new Road("East");
    static Road west = new Road("West");

    static Road currentRoad = north;

    static int remainingTime = 30;

    static boolean running = true;

    static Road[] roads = {
        north, south, east, west
    };

    static int calculateGreenTime(int vehicles, int totalVehicles) {

        int minimumTime = 10;
        int maximumExtraTime = 30;

        if (totalVehicles == 0) {
            return 30;
        }

        int time =
            minimumTime +
            (vehicles * maximumExtraTime / totalVehicles);

        return Math.min(time, 40);
    }

    static int getInteger(Scanner sc, String message) {

        while (true) {

            System.out.print(message);

            if (sc.hasNextInt()) {

                int value = sc.nextInt();

                if (value >= 0) {
                    return value;
                }

                System.out.println(
                    "Enter a non-negative integer."
                );

            } else {

                System.out.println(
                    "Enter integer values only."
                );

                sc.next();
            }
        }
    }

    static void updateTraffic(Scanner sc) {

        north.vehicles =
            getInteger(sc, "Enter North vehicles: ");

        south.vehicles =
            getInteger(sc, "Enter South vehicles: ");

        east.vehicles =
            getInteger(sc, "Enter East vehicles: ");

        west.vehicles =
            getInteger(sc, "Enter West vehicles: ");

        System.out.println(
            "Traffic updated successfully."
        );
    }

    static void displayTraffic() {

        System.out.println();
        System.out.println("----- TRAFFIC STATUS -----");

        System.out.println(
            "North : " + north.vehicles + " vehicles"
        );

        System.out.println(
            "South : " + south.vehicles + " vehicles"
        );

        System.out.println(
            "East  : " + east.vehicles + " vehicles"
        );

        System.out.println(
            "West  : " + west.vehicles + " vehicles"
        );

        System.out.println(
            "Green Signal : " + currentRoad.name
        );

        System.out.println(
            "Time Remaining : "
            + remainingTime
            + " seconds"
        );
    }

    static void updateTimerOnly() {

        System.out.print("\033[s");

        System.out.print("\033[1;1H");

        System.out.println(
            "================================"
        );

        System.out.println(
            "     ADAPTIVE TRAFFIC SIGNAL"
        );

        System.out.println(
            "================================"
        );

        System.out.println(
            "Green Signal : "
            + currentRoad.name
            + "                    "
        );

        System.out.println(
            "Time Remaining : "
            + remainingTime
            + " seconds          "
        );

        System.out.println(
            "================================"
        );

        System.out.print("\033[u");

        System.out.flush();
    }

    static void startTimer() {

        Thread timerThread = new Thread(() -> {

            while (running) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }

                remainingTime--;

                if (remainingTime <= 0) {

                    int currentIndex = 0;

                    for (int i = 0; i < roads.length; i++) {

                        if (roads[i] == currentRoad) {
                            currentIndex = i;
                            break;
                        }
                    }

                    currentRoad =
                        roads[(currentIndex + 1) % roads.length];

                    int totalVehicles =
                        north.vehicles +
                        south.vehicles +
                        east.vehicles +
                        west.vehicles;

                    if (totalVehicles > 0) {

                        remainingTime =
                            calculateGreenTime(
                                currentRoad.vehicles,
                                totalVehicles
                            );

                    } else {

                        remainingTime = 30;
                    }
                }

                updateTimerOnly();
            }
        });

        timerThread.setDaemon(true);
        timerThread.start();
    }

    static void selectRoad() {

        Road emergencyRoad = null;

        for (Road road : roads) {

            if (road.emergency) {
                emergencyRoad = road;
                break;
            }
        }

        if (emergencyRoad != null) {

            currentRoad = emergencyRoad;
            remainingTime = 30;

            System.out.println();
            System.out.println(
                "Emergency Priority: "
                + currentRoad.name
            );

            currentRoad.vehicles =
                Math.max(
                    0,
                    currentRoad.vehicles - 10
                );

            System.out.println(
                "10 vehicles passed from "
                + currentRoad.name
            );

            System.out.println(
                "Time reset to 30 seconds."
            );

            updateTimerOnly();

            return;
        }

        int totalVehicles =
            north.vehicles +
            south.vehicles +
            east.vehicles +
            west.vehicles;

        if (totalVehicles == 0) {

            System.out.println();
            System.out.println(
                "No vehicles on any road."
            );

            return;
        }

        Road selectedRoad = roads[0];

        for (Road road : roads) {

            if (road.vehicles >
                selectedRoad.vehicles) {

                selectedRoad = road;
            }
        }

        currentRoad = selectedRoad;

        int greenTime =
            calculateGreenTime(
                currentRoad.vehicles,
                totalVehicles
            );

        remainingTime = greenTime;

        System.out.println();
        System.out.println(
            "Adaptive Signal: "
            + currentRoad.name
        );

        System.out.println(
            "Green Time: "
            + greenTime
            + " seconds"
        );

        currentRoad.vehicles =
            Math.max(
                0,
                currentRoad.vehicles - 10
            );

        System.out.println(
            "10 vehicles passed from "
            + currentRoad.name
        );

        System.out.println(
            "Remaining vehicles on "
            + currentRoad.name
            + ": "
            + currentRoad.vehicles
        );

        updateTimerOnly();
    }

    static void emergency(Scanner sc) {

        System.out.println();
        System.out.println(
            "Select Emergency Road:"
        );

        System.out.println("1. North");
        System.out.println("2. South");
        System.out.println("3. East");
        System.out.println("4. West");

        int choice =
            getInteger(
                sc,
                "Enter choice: "
            );

        north.emergency = false;
        south.emergency = false;
        east.emergency = false;
        west.emergency = false;

        switch (choice) {

            case 1:
                north.emergency = true;
                break;

            case 2:
                south.emergency = true;
                break;

            case 3:
                east.emergency = true;
                break;

            case 4:
                west.emergency = true;
                break;

            default:
                System.out.println(
                    "Invalid choice."
                );
                return;
        }

        selectRoad();
    }

    static void printMenu() {

        System.out.println();
        System.out.println("1. Update Traffic");
        System.out.println("2. Display Traffic");
        System.out.println("3. Select Adaptive Signal");
        System.out.println("4. Emergency Priority");
        System.out.println("5. Exit");
        System.out.println("================================");
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        currentRoad = north;
        remainingTime = 30;

        System.out.println(
            "================================"
        );

        System.out.println(
            "     ADAPTIVE TRAFFIC SIGNAL"
        );

        System.out.println(
            "================================"
        );

        System.out.println(
            "Green Signal : "
            + currentRoad.name
        );

        System.out.println(
            "Time Remaining : "
            + remainingTime
            + " seconds"
        );

        System.out.println(
            "================================"
        );

        startTimer();

        int choice;

        do {

            printMenu();

            choice =
                getInteger(
                    sc,
                    "Enter choice: "
                );

            switch (choice) {

                case 1:
                    updateTraffic(sc);
                    break;

                case 2:
                    displayTraffic();
                    break;

                case 3:
                    selectRoad();
                    break;

                case 4:
                    emergency(sc);
                    break;

                case 5:
                    running = false;

                    System.out.println();
                    System.out.println(
                        "Simulation stopped."
                    );

                    break;

                default:
                    System.out.println(
                        "Invalid choice."
                    );
            }

        }
        while (choice != 5);
    }
}
