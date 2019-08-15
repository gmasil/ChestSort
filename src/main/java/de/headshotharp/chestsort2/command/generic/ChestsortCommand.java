package de.headshotharp.chestsort2.command.generic;

public interface ChestsortCommand extends CommandRunnable, CommandApplicable, CommandTabCompletable {
	public boolean isForPlayerOnly();

	public String usage();

	public String getName();
}
