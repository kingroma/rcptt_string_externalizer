package com.java.util;

public class __ProgressBar {
	
//	ProgressBar bar = new ProgressBar(); // ProgressBar 가 필요한경우
//	bar.update(0, object_size); // ProgressBar 가 필요한경우
//	if(i%7==0) // ProgressBar 가 필요한경우
//	bar.update(i, object_size); // ProgressBar 가 필요한경우
	
	
	private StringBuilder progress;
	
	public __ProgressBar() {
        init();
    }
	
	public void update(int done, int total) {
        char[] workchars = {'|', '/', '-', '\\'};
        String format = "\r%3d%% %s %c";

        int percent = (++done * 100) / total;
        int extrachars = (percent / 2) - this.progress.length();

        while (extrachars-- > 0) {
            progress.append('#');
        }

        System.out.printf(format, percent, progress,
         workchars[done % workchars.length]);

        if (done == total) {
            System.out.flush();
            System.out.println();
            init();
        }
    }

    private void init() {
        this.progress = new StringBuilder(60);
    }
	
}
