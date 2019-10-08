package de.headshotharp.chestsort.command.generic;

public interface ChestsortCommand extends CommandRunnable, CommandApplicable, CommandTabCompletable {
	public boolean isForPlayerOnly();

	public String usage();

	public String getName();
}
