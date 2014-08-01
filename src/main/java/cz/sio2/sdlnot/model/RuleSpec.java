package cz.sio2.sdlnot.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mindswap.pellet.utils.FileUtils;

/**
 * @author Petr Křemen
 */
public class RuleSpec {

	private static String RULE_SPEC_FILE_NAME = "rulespec";

	private File currentDir;
	
	private List<Rule> ruleList = new ArrayList<Rule>();

	private Map<String, Rule> nameToRuleMap = new HashMap<String, Rule>();

	private List<RuleSpecListener> listeners = new ArrayList<RuleSpecListener>();
	
	public List<Rule> getRuleList() {
		return Collections.unmodifiableList(ruleList);
	}

	public Rule getRuleForName(String ruleName) {
		return nameToRuleMap.get(ruleName);
	}

	public void addListener(RuleSpecListener listener) {
		if ( !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(RuleSpecListener listener) {
		if ( listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
	
	private void notifyRuleAdded(Rule rule) {
		for(RuleSpecListener l : listeners ) {
			try {
				l.notifyRuleAdded(rule);
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}
	
	private void notifyRuleRemoved(Rule rule) {
		for(RuleSpecListener l : listeners ) {
			try {
				l.notifyRuleRemoved(rule);
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}
	
	private void notifyRuleMovedUp(int i, Rule rule) {
		for(RuleSpecListener l : listeners ) {
			try {
				l.notifyRuleMovedUp(i, rule);
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}

	public void addRule(final Rule rule) {
		ruleList.add(rule);
		nameToRuleMap.put(rule.getName(),rule);
		notifyRuleAdded(rule);
	}
	
	public void removeRule(final Rule rule) {
		ruleList.remove(rule);
		nameToRuleMap.remove(rule.getName());
		new File(getQueryFileName(rule)).delete();
		notifyRuleRemoved(rule);
	}	

	public void moveupRule(Rule rule) {
		int i = ruleList.indexOf(rule);
		if (i > 0) {
			ruleList.remove(i);
			ruleList.add(i-1, rule);			
			notifyRuleMovedUp(i,rule);
		}
	}	
	
	public void clear() {
		ruleList.clear();
		nameToRuleMap.clear();
	}
	
	public void reload() {
		reload(currentDir);
	}
	
	private void reload(final File currentDir) {
		final File ruleSpecFile = new File(currentDir, RULE_SPEC_FILE_NAME);
		BufferedReader br = null;
		clear();
		try {
			br = new BufferedReader(new FileReader(ruleSpecFile));
			if (!ruleSpecFile.createNewFile()) {
				String line;
				while ((line = br.readLine()) != null) {
					// load rule reference
					final Rule rule = Rule.createRuleReferenceFromString(line);
					addRule(rule);
					// load actual rule
					rule.reload(currentDir);
				}
			}
		} catch (IOException e ) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}

	public void saveAll() {
		saveToDir(currentDir);
	}
	
	private void saveToDir(final File currentDir) {
		final File ruleSpecFile = new File(currentDir, RULE_SPEC_FILE_NAME);
		BufferedWriter fs = null;
		try {
			fs = new BufferedWriter(new FileWriter(ruleSpecFile));
			for (final Rule rule : ruleList) {
				// save rule reference
				fs.write(Rule.createStringForRuleReference(rule));
				fs.newLine();
				rule.save(getCurrentDir());
			}
			fs.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public int indexOf(final String ruleName) {
		final Rule rule = nameToRuleMap.get(ruleName);
		if ( rule == null ) {
			return -1;
		} else {
			return ruleList.indexOf(rule);
		}
	}
	
	public Rule getRuleAtIndex( int i ) {
		return ruleList.get(i);
	}
	
	public int getActiveCount() {
		int i = 0;
		for(Rule r : ruleList) {
			if (r.isActive()) {
				i++;
			}
		}
		return i;
	}
	
	public File getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(File currentDir) {
		this.currentDir = currentDir;
	}
	
	private String getQueryFileName(final Rule rule) {
		return currentDir + File.separator + rule.getName();
	}
	
	public File getOutputDir() {
		return new File(currentDir + File.separator + "output");
	}
	
	public File getResultFile(final Rule rule) {
		return new File(getOutputDir().getPath() + File.separator
				+ rule.getName() + ".owl");
	}
	
	public void revert(Rule rule) throws IOException {
		try {
			rule.setRuleString(FileUtils.readFile(getQueryFileName(rule)));			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}