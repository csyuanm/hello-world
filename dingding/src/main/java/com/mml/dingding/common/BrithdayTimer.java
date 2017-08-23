package com.mml.dingding.common;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BrithdayTimer {
	
	Log log = LogFactory.getLog(BrithdayTimer.class);
	
	
		static int count = 0;

		public static void showTimer() {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					++count;
					System.out.println("count执行了-->" + count+"次"); // 1次
				}

			};

			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			/*** 定制每日00：24：00执行方法 ***/
			calendar.set(2017, 8, 16, 15, 46, 00);
			Date date = calendar.getTime();
			Timer timer = new Timer();
			timer.schedule(task, 1000);
		}

		public static void main(String[] args) {
			showTimer();
		}

	

}
	