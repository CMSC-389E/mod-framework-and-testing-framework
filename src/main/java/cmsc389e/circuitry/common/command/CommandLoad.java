package cmsc389e.circuitry.common.command;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;

import cmsc389e.circuitry.common.ConfigCircuitry;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

/**
 * TODO
 */
public final class CommandLoad extends CommandCircuitryBase {
    private static boolean[][] tests;

    /**
     * TODO
     *
     * @param world         TODO
     * @param sender        TODO
     * @param projectNumber TODO
     * @throws CommandException TODO
     */
    public static void execute(@SuppressWarnings("unused") World world, ICommandSender sender, Integer projectNumber)
	    throws CommandException {
	// Check if submit.jar exists and download a new one if it doesn't
	if (Files.notExists(Paths.get(ConfigCircuitry.submit)))
	    try {
		FileUtils.copyURLToFile(new URL(ConfigCircuitry.server + "submit.jar"),
			new File(ConfigCircuitry.submit));
	    } catch (IOException e) {
		throw new CommandException("Could not download submit.jar from " + ConfigCircuitry.server + '.');
	    }

	// Attempt to download tests.txt
	sendMessage(sender,
		"Attempting to read project file " + projectNumber + " from " + ConfigCircuitry.server + '.');
	try {
	    FileUtils.copyURLToFile(new URL(ConfigCircuitry.server + "proj" + projectNumber + "tests.txt"),
		    new File(ConfigCircuitry.tests));
	} catch (MalformedURLException e) {
	    throw new CommandException("Project " + projectNumber + " is not valid.");
	} catch (IOException e) {
	    throw new CommandException(ConfigCircuitry.submit + " is missing or " + ConfigCircuitry.tests
		    + " is being used by another process.  Try running again.");
	}
	sendMessage(sender,
		"Successfully read project file " + projectNumber + " from " + ConfigCircuitry.server + '.');

	// Load in valid tags and rows for the current test
	getTests();

	sendMessage(sender, "Loaded passed file in correctly.");
    }

    /**
     * TODO
     *
     * @return TODO
     * @throws CommandException TODO
     */
    public static boolean[][] getTests() throws CommandException {
	if (tests == null)
	    try {
		List<String> lines = Files.readAllLines(Paths.get(ConfigCircuitry.tests));
		String[] tags = lines.get(1).split("\t(?=o)", 2);
		ConfigCircuitry.inTags = tags[0].split("\t");
		ConfigCircuitry.outTags = tags[1].split("\t");
		ConfigCircuitry.sync();
		tests = new boolean[lines.size() - 2][ConfigCircuitry.inTags.length + ConfigCircuitry.outTags.length];
		for (int i = 2; i < lines.size(); i++) {
		    tags = lines.get(i).split("\t");
		    for (int j = 0; j < tags.length; j++)
			tests[i - 2][j] = tags[j].equals("1");
		}
	    } catch (IOException e) {
		throw new CommandException("Unable to read " + ConfigCircuitry.tests + ". Try running /load again.");
	    }
	return tests;
    }

    /**
     * TODO
     */
    public CommandLoad() {
	super("load");
    }
}