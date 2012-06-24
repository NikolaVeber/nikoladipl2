package gov.nasa.jpf.symbc;

public class DoubleTest extends InvokeTest {

  // x > 1.1
  protected static String PC1 = "# = 1\nx_1_SYMREAL > CONST_1.1";
  //
  // (x <= 1.1)
  protected static String PC2 = "x_1_SYMREAL < CONST_1.1";
  protected static String PC3 = "CONST_1.1 == x_1_SYMREAL";
  //
  // [(x > 1.1) && ((z := y) > 30.0)] || [(x < 1.1) && ((z := x+y) > 30.0)] || [(x == 1.1) && ((z := x+y) > 30.0)]
  protected static String PC4 = "(x_1_SYMREAL + y_2_SYMREAL) > CONST_30.0";
  protected static String PC5 = "y_2_SYMREAL > CONST_30.0";
  //
  // [((z := x+y) < 30.0) && (x == 1.1)] || [(x < 1.1) && ((z := x+y) < 30.0)] ||
  // [(x < 1.1) && ((z := x+y) == 30.0)] || [(x == 1.1) && ((z := x+y) == 30.0)] ||
  // [(x > 1.1) && ((z := y) < 30.0)] || [(x > 1.1) && ((z := y) == 30.0)]
  protected static String PC6 = "CONST_30.0 == (x_1_SYMREAL + y_2_SYMREAL)";
  protected static String PC7 = "(x_1_SYMREAL + y_2_SYMREAL) < CONST_30.0";
  protected static String PC8 = "y_2_SYMREAL < CONST_30.0";
  protected static String PC9 = "CONST_30.0 == y_2_SYMREAL";

  protected static void testDouble(double x, double y) {
    double z = x + y;

    if (x > 1.1) {
      assert pcMatches(PC1) : makePCAssertString("TestDoubleSpecial1.testDouble1 if x > 1.1", PC1, TestUtils.getPathCondition());
      z = y;
    } else {
      assert (pcMatches(PC2) || pcMatches(PC3)) : makePCAssertString("TestDoubleSpecial1.testDouble1 x <= 1.1",
              "either\n" + PC2 + "\nor\n" + PC3, TestUtils.getPathCondition());
    }
    String pc = TestUtils.getPathCondition();
    if (z > 30.0) {
      assert (pcMatches(joinPC(PC4, pc)) || pcMatches(joinPC(PC5, pc))) : makePCAssertString(
              "TestDoubleSpecial1.testDouble1 z > 30.0", "one of\n" + PC4 + "\nor\n"
              + PC5, TestUtils.getPathCondition());
      z = 91.0;
    } else {
      assert (pcMatches(joinPC(PC6, pc)) || pcMatches(joinPC(PC7, pc)) || pcMatches(joinPC(PC8, pc)) || pcMatches(joinPC(PC9, pc))) : makePCAssertString(
              "TestDoubleSpecial1.testDouble1 z <= 30.0", "one of\n" + joinPC(PC6, pc) + "\nor\n"
              + joinPC(PC7, pc) + "\nor\n" + joinPC(PC8, pc) + "\nor\n" + joinPC(PC9, pc),
              TestUtils.getPathCondition());
    }
  }
}
