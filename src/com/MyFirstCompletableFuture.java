package com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class MyFirstCompletableFuture {
	private ExecutorService service = Executors.newFixedThreadPool(10);

	public CompletableFuture<?>[] createMultipleReadFileAsync(String file,
			ExecutorService service, Integer multipleNum) {
		CompletableFuture<?>[] futures = new CompletableFuture[multipleNum];
		for (int i = 0; i < multipleNum; i++) {
			futures[i] = readFileAsync(file, service).whenComplete(
					(ok, ex) -> {
						if (ex == null) {
							System.out.println(ok);
						} else {
							ex.printStackTrace();
						}
					});
		}
		return futures;
	}

	public CompletableFuture<String> readFileAsync(String file,
			ExecutorService service) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return new String(Files.readAllBytes(Paths.get(file)));
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}, service);
	}

	public CompletableFuture<String> oldSchoolReadFileAsync(String file,
			ExecutorService service) {
		return CompletableFuture.supplyAsync(new Supplier<String>() {
			@Override
			public String get() {
				try {
					return new String(Files.readAllBytes(Paths.get(file)));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		}, service);
	}

	public ExecutorService getExecutorService() {
		return service;
	}

	public static void main(String[] args) throws Exception {
		MyFirstCompletableFuture mfe = new MyFirstCompletableFuture();
		CompletableFuture
				.allOf(mfe.createMultipleReadFileAsync("Lab.txt",
						mfe.getExecutorService(), 100))
				.whenComplete((results, exception) -> {
					onAllFutureComplete(exception);
				}).join(); // join 是為了避免 main執行緒在任務完成前就關閉ExecutorService
		System.out.println("Send All The Thread Out.");
		mfe.shutdownExecutorService();
	}

	private static void onAllFutureComplete(Throwable exception) {
		if (exception == null) {
			showFinalResult();
		} else {
			showFinalResult();
		}
	}

	private static void showFinalResult() {
		System.out.println("All Thread job down");
	}

	public void shutdownExecutorService() {
		System.out.println("ShutingDown the ExecutorService.");
		service.shutdown();
	}
}
