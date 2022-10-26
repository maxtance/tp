package seedu.classify.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.classify.commons.core.Messages;
import seedu.classify.logic.commands.exceptions.CommandException;
import seedu.classify.logic.parser.CliSyntax;
import seedu.classify.model.Model;
import seedu.classify.model.student.Class;
import seedu.classify.model.student.ClassPredicate;
import seedu.classify.model.student.GradeComparator;
import seedu.classify.model.student.GradeLessThanMeanPredicate;
import seedu.classify.model.student.exceptions.ExamNotFoundException;

/**
 * Calculates the mean of a particular exam from a particular class, using the given class and given exam. Returns the
 * list of students in the class, sorted by the grade of the particular exam, and, if specified, shows only the
 * students whose grade falls below the calculated mean.
 */
public class ViewStatsCommand extends Command {

    public static final String COMMAND_WORD = "viewstats";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Retrieves the grades of the specified exam from the"
            + " students in the specified class, calculates the mean score within the class, then sorts the class list"
            + " according to grade. If filter tag is on, only students whose grade falls below the mean will be"
            + " displayed.\n"
            + "Parameters: " + CliSyntax.PREFIX_CLASS + "CLASS " + CliSyntax.PREFIX_EXAM + "EXAM filter/FILTER\n"
            + "Example: " + COMMAND_WORD + " class/4A1 exam/SA1 filter/on";

    private final Class className;
    private final String exam;
    private final boolean isFilterOn;

    /**
     * creates a ViewStatsCommand to view the mean of a particular exam of a particular class,
     * using the specified fields.
     */
    public ViewStatsCommand(Class className, String exam, boolean isFilterOn) {
        this.className = className;
        this.exam = exam;
        this.isFilterOn = isFilterOn;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        //execute a ViewClassCommand to get the class of interest
        ViewClassCommand viewClassCommand = new ViewClassCommand(new ClassPredicate(className));
        viewClassCommand.execute(model);
        if (model.getFilteredStudentList().size() == 0) {
            throw new CommandException(Messages.MESSAGE_STUDENT_CLASS_NOT_FOUND);
        } else {
            try {
                model.sortStudentRecord(new GradeComparator(exam, className));
                double mean = model.calculateExamMean(exam);
                if (isFilterOn) {
                    model.updateFilteredStudentList(new GradeLessThanMeanPredicate(className, mean, exam));
                }
                return new CommandResult(String.format(Messages.MESSAGE_CLASS_SORTED_BY_GRADE, className)
                        + String.format(Messages.MESSAGE_DISPLAY_MEAN, exam, className, mean));
            } catch (ExamNotFoundException e) {
                throw new CommandException(e.getMessage()
                        + "\nMean cannot be calculated, class will be sorted by alphabetical order");
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ViewStatsCommand // instanceof handles nulls
                && className.equals(((ViewStatsCommand) other).className)
                && exam.equals(((ViewStatsCommand) other).exam)
                && isFilterOn == ((ViewStatsCommand) other).isFilterOn); // state check
    }
}
