import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public class CustomProblem extends AbstractProblem {

  public CustomProblem() {
    super(2, 2, 2);
  }

  @Override
  public void evaluate(Solution solution) {
    double x = ((RealVariable)solution.getVariable(0)).getValue();
    double y = ((RealVariable)solution.getVariable(1)).getValue();
    double f1 = -2.0*x + y;
    double f2 = 2.0*x + y;
    double c1 = -x + y - 1.0;
    double c2 = x + y - 7.0;

    solution.setObjective(0, f1);
    solution.setObjective(1, f2);
    solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
    solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
  }

  @Override
  public Solution newSolution() {
    Solution solution = new Solution(2, 2, 2);

    solution.setVariable(0, new RealVariable(0.0, 5.0));
    solution.setVariable(1, new RealVariable(0.0, 3.0));

    return solution;
  }

}

