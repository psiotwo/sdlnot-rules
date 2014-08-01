package cz.sio2.sdlnot.model;

public interface RuleSpecListener {

	public void notifyRuleAdded(final Rule rule);

	public void notifyRuleRemoved(final Rule rule);

	public void notifyRuleMovedUp(final int index, final Rule rule);
}
