package com.mirrorlabs.filebrowser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;

import android.util.Log;

public class CMDProcessor {

	private static final String LD_LIBRARY_PATH = System.getenv("LD_LIBRARY_PATH");
	private static final String TAG = "CMD Processor";
	private Boolean can_su;
	public SH sh;
	public SH su;

	public CMDProcessor() {
		sh = new SH("sh");
		su = new SH("su");
	}

	public SH suOrSH() {
		return canSU() ? su : sh;
	}

	public boolean canSU() {
		return canSU(false);
	}

	public class CommandResult {
		public final String stdout;
		public final String stderr;
		public final Integer exit_value;

		CommandResult(final Integer exit_value_in) {
			this(exit_value_in, null, null);
		}

		CommandResult(final Integer exit_value_in, final String stdout_in,
				final String stderr_in) {
			exit_value = exit_value_in;
			stdout = stdout_in;
			stderr = stderr_in;
		}

		public boolean success() {
			return exit_value != null && exit_value == 0;
		}
	}

	public class SH {
		private String SHELL = "sh";

		public SH(final String SHELL_in) {
			SHELL = SHELL_in;
		}

		private String getStreamLines(final InputStream is) {
			String out = null;
			StringBuffer buffer = null;
			final DataInputStream dis = new DataInputStream(is);

			try {
				if (dis.available() > 0) {
					buffer = new StringBuffer(dis.readLine());
					while (dis.available() > 0) {
						buffer.append("\n").append(dis.readLine());
					}
				}
				dis.close();
			} catch (final Exception ex) {
				Log.e(TAG, ex.getMessage());
			}
			if (buffer != null) {
				out = buffer.toString();
			}
			return out;
		}

		public Process run(final String cmd) {
			Process process = null;
			Runtime runtime = Runtime.getRuntime();
			try {
				process = runtime.exec(SHELL);
				final DataOutputStream toProcess = new DataOutputStream(
						process.getOutputStream());
				// On some versions of Android (ICS) LD_LIBRARY_PATH is unset when using su
				// We need to pass LD_LIBRARY_PATH over su for some commands to work correctly.
				String setenv = "";
				if ("su".equals(SHELL)) {
					setenv = "LD_LIBRARY_PATH=" + LD_LIBRARY_PATH + " ";
				}
				toProcess.writeBytes(setenv + "exec " + cmd + "\n");
				toProcess.flush();
			} catch (final Exception e) {
				Log.e(TAG, "Exception while trying to run: '" + cmd + "' "
						+ e.getMessage());
				process = null;
			}
			return process;
		}

		public CommandResult runWaitFor(final String s) {
			final Process process = run(s);
			Integer exit_value = null;
			String stdout = null;
			String stderr = null;
			if (process != null) {
				try {
					exit_value = process.waitFor();
					stdout = getStreamLines(process.getInputStream());
					stderr = getStreamLines(process.getErrorStream());
				} catch (final InterruptedException e) {
					Log.e(TAG, "runWaitFor " + e.toString());
				} catch (final NullPointerException e) {
					Log.e(TAG, "runWaitFor " + e.toString());
				}
			}
			return new CommandResult(exit_value, stdout, stderr);
		}

		public Process run(final String[] cmds) {
			Process process = null;
			try {
				process = Runtime.getRuntime().exec(SHELL);
				final DataOutputStream toProcess = new DataOutputStream(
						process.getOutputStream());
				// On some versions of Android (ICS) LD_LIBRARY_PATH is unset when using su
				// We need to pass LD_LIBRARY_PATH over su for some commands to work correctly.
				String setenv = "";
				if (SHELL.equals("su")) {
					setenv = "LD_LIBRARY_PATH=" + LD_LIBRARY_PATH + " ";
				}
				for (String cmd : cmds) {
					toProcess.writeBytes(setenv + cmd + "\n");
				}
				toProcess.writeBytes("exit\n");
				toProcess.flush();
			} catch (final Exception e) {
				Log.e(TAG, "Exception while trying to run cmds"
						+ e.getMessage());
				process = null;
			}
			return process;
		}

		public CommandResult runWaitFor(final String cmds[]) {
			final Process process = run(cmds);
			Integer exit_value = null;
			String stdout = null;
			String stderr = null;
			if (process != null) {
				try {
					exit_value = process.waitFor();
					stdout = getStreamLines(process.getInputStream());
					stderr = getStreamLines(process.getErrorStream());
				} catch (final InterruptedException e) {
					Log.e(TAG, "runWaitFor " + e.toString());
				} catch (final NullPointerException e) {
					Log.e(TAG, "runWaitFor " + e.toString());
				}
			}
			return new CommandResult(exit_value, stdout, stderr);
		}
	}

	public boolean canSU(final boolean force_check) {
		if (can_su == null || force_check) {
			final CommandResult r = su.runWaitFor("id");
			final StringBuilder out = new StringBuilder();

			if (r.stdout != null) {
				out.append(r.stdout).append(" ; ");
			}
			if (r.stderr != null) {
				out.append(r.stderr);
			}
             
			Log.d(TAG, "canSU() su[" + r.exit_value + "]: " + out);
			can_su = r.success();
		}
		return can_su;
	}
}