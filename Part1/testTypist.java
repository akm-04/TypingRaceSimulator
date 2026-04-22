public class testTypist {

    public static void main(String[] args) {
        System.out.println("=== TYPIST CLASS TEST FRAMEWORK ===\n");

        // Call other tests are required. comment functions to disable test
        testProgressFloor();
        testBurnoutCountdown();
        testAccuracySetterClamping();
        testConstructorValidation();
        testNormalForwardMovement();
        testResetToStart();

        System.out.println("=== ALL TESTS COMPLETED ===");
    }

    // TEST METHODS
    private static void testProgressFloor() {
        // Test if progressbar does not go below zero
        System.out.println("========== Test A Started: Progress Floor Test ==========");
        System.out.println();
        Typist t = new Typist('T', "TEST_SUBJECT", 0.5);
        
        System.out.println("Current Progress: " + t.getProgress());
        t.slideBack(5); // expected: 0, should not go below zero
        System.out.println("Progress after slideBack(5): " + t.getProgress());
        System.out.println();
        System.out.println("================= Test A Completed =================\n");
        System.out.println("\n");
    }

    private static void testBurnoutCountdown() {
        // Test burnout countdown
        System.out.println("========== Test B Started: Burnout Countdown ==========");
        System.out.println();
        Typist t = new Typist('T', "TEST_SUBJECT", 0.5);
        
        t.burnOut(2);
        System.out.println("Triggered Burnout(2). isBurntOut: " + t.isBurntOut() + ", Turns: " + t.getBurnoutTurnsRemaining());
        
        t.recoverFromBurnout();
        System.out.println("After 1 recovery call. isBurntOut: " + t.isBurntOut() + ", Turns: " + t.getBurnoutTurnsRemaining());
        
        t.recoverFromBurnout();
        System.out.println("After 2 recovery calls. isBurntOut: " + t.isBurntOut() + ", Turns: " + t.getBurnoutTurnsRemaining());
        System.out.println();
        System.out.println("================= Test B Completed =================\n");
        System.out.println("\n");
    }

    private static void testAccuracySetterClamping() {
        // Test accuracy setter clamping
        System.out.println("========== Test C Started: Accuracy Clamping ==========");
        System.out.println();
        Typist t = new Typist('T', "TEST_SUBJECT", 0.5);
        
        t.setAccuracy(1.5);
        System.out.println("Attempted to set accuracy to 1.5. Actual accuracy: " + t.getAccuracy());
        
        t.setAccuracy(-0.5);
        System.out.println("Attempted to set accuracy to -0.5. Actual accuracy: " + t.getAccuracy());
        System.out.println();
        System.out.println("================= Test C Completed =================\n");
        System.out.println("\n");
    }

    private static void testConstructorValidation() {
        // Test constructor validation
        System.out.println("========== Test C2 Started: Constructor Validation ==========");
        System.out.println();
        Typist t = new Typist('X', "INVALID", 2.0);
        
        System.out.println("Attempted to create typist with 2.0 accuracy. Actual: " + t.getAccuracy());
        System.out.println();
        System.out.println("================= Test C2 Completed =================\n");
        System.out.println("\n");
    }

    private static void testNormalForwardMovement() {
        // Test if progressbar moves forward when typist types

        System.out.println("========== Test D Started: Normal Forward Movement ==========");
        System.out.println();
        Typist t = new Typist('T', "TEST_SUBJECT", 0.5);
        
        System.out.println("Initial Progress: " + t.getProgress());
        t.typeCharacter();
        t.typeCharacter();
        System.out.println("Progress after 2 typeCharacter() calls: " + t.getProgress());
        System.out.println();
        System.out.println("================= Test D Completed =================\n");
        System.out.println("\n");
    }

    private static void testResetToStart() {
        // Progressbar should reset to 0 and burnout should be cleared
        System.out.println("========== Test E Started: Reset To Start ==========");
        System.out.println();
        Typist t = new Typist('T', "TEST_SUBJECT", 0.5);
        
        // Dirty the state
        t.typeCharacter();
        t.burnOut(3);
        System.out.println("Before Reset - Progress: " + t.getProgress() + ", isBurntOut: " + t.isBurntOut());
        
        // Clean the state
        t.resetToStart();
        System.out.println("After Reset  - Progress: " + t.getProgress() + ", isBurntOut: " + t.isBurntOut() + ", Turns: " + t.getBurnoutTurnsRemaining());
        System.out.println();
        System.out.println("================= Test E Completed =================\n");
        System.out.println("\n");
    }
}