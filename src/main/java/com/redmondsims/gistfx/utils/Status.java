package com.redmondsims.gistfx.utils;

import com.redmondsims.gistfx.enums.State;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static com.redmondsims.gistfx.enums.State.*;

public class Status {

	private static       State                          state              = NORMAL;
	private static final ConcurrentLinkedDeque<Integer> fileList           = new ConcurrentLinkedDeque<>();
	private static final AtomicInteger                  maxFilesRegistered = new AtomicInteger(0);

	public static void setState(State state) {
		Status.state = state;
	}

	public static State getState () {
		return state;
	}

	public static boolean stateIs(State state) {
		return Status.state.equals(state);
	}

	public static boolean isComparing() {
		return state.equals(COMPARING) || state.equals(LOADING);
	}

	public static void register(Integer fileId) {
		fileList.add(fileId);
		maxFilesRegistered.incrementAndGet();
	}

	public static boolean filesComparing() {
		return fileList.size() > 0;
	}

	public static void unRegister(Integer fileId) {
		fileList.remove(fileId);
	}

	public static double getRegisteredFileRatio() {
		double inverseNumber = maxFilesRegistered.get() - fileList.size();
		return inverseNumber / maxFilesRegistered.get();
	}

}
