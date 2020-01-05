package com.example.caricatur;

public enum FaceLandmarks {
    BOTTOM_MOUTH,
    LEFT_CHEEK,
    LEFT_EAR,
    LEFT_EAR_TIP,
    LEFT_EYE,
    LEFT_MOUTH,
    NOSE_BASE,
    RIGHT_CHEEK,
    RIGHT_EAR,
    RIGHT_EAR_TIP,
    RIGHT_EYE,
    RIGHT_MOUTH;

    static FaceLandmarks convert(int n) {
        switch(n){
            case 0:
                return BOTTOM_MOUTH;
            case 1:
                return LEFT_CHEEK;
            case 2:
                return LEFT_EAR_TIP;
            case 3:
                return LEFT_EAR;
            case 4:
                return LEFT_EYE;
            case 5:
                return LEFT_MOUTH;
            case 6:
                return NOSE_BASE;
            case 7:
                return RIGHT_CHEEK;
            case 8:
                return RIGHT_EAR_TIP;
            case 9:
                return RIGHT_EAR;
            case 10:
                return RIGHT_EYE;
            case 11:
                return RIGHT_MOUTH;
            default:
                return null;
        }
    }
}
