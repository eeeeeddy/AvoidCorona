package com.example.user.capstone.helper;

import com.example.user.capstone.R;

public class AnimationDirection {
    //화면이 옆에서 부드럽게 밀려들어오는 느낌으로 화면전환을 시키는 클래스이다.


    public enum Direction {
        //ENTER_LEFT 화면전환시 왼쪽에서 slide 퇴장 효과의 애니메이션션     ENTER_RIGHT 화면전환시 오른쪽에서 왼쪽으로 slide 퇴장 효과의 애니메이션
        //EXIT_LEFT 해당화면 종료시 왼쪽으로 slide 퇴장 효과의 애니메이션    EXIT_RIGHT 해당화면 종료시 오른으로 slide 퇴장 효과의 애니메이션
        ENTER_LEFT(R.anim.slide_in_from_left_ani, R.anim.fade_out_ani), ENTER_RIGHT(R.anim.slide_in_from_right_ani, R.anim.fade_out_ani),
        EXIT_RIGHT(R.anim.fade_in_ani, R.anim.slide_out_to_right_ani), EXIT_LEFT(R.anim.fade_in_ani, R.anim.slide_out_to_left_ani);

        private final int enterAni;
        private final int exitAni;

        Direction(int enterAni, int exitAni) {
            this.enterAni = enterAni;
            this.exitAni = exitAni;
        }

        public int getEnterAni() {
            return enterAni;
        }

        public int getExitAni() {
            return exitAni;
        }

    }
}
