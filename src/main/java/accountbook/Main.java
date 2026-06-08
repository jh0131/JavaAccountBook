package accountbook;

import accountbook.view.AccountBookFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        /* Swing의 GUI 전용 스레드인 EDT(Event Dispatch Thread)에 화면 생성
        Swing은 기본적으로 스레드 안전하지 않아서 GUI 작업을 반드시 EDT에서 처리하는 게 권장 방식
        new Runnable() 내부에서 run() 실행  */

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                AccountBookFrame frame = new AccountBookFrame();
                frame.setVisible(true);

            }
        });
    }
}
