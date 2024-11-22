#include <stdio.h>
#include "usac.h"

int main(){
    int option = -1;
    do{
        printf("Choose an option:\n");
        printf("1 - Get data from a string\n");
        printf("2 - Get Binary number\n");
        printf("3 - Get number\n");
        printf("4 - Format command\n");
        printf("5 - Insert values into buffer\n");
        printf("6 - Remove values from buffer\n");
        printf("7 - Get Number of elements from buffer\n");
        printf("8 - Remove n elements from buffer\n");
        printf("9 - Sort Array\n");
        printf("10 - Get Median of an array\n");
        printf("0 - Exit\n");
        scanf("%d", &option);
        switch(option){
            case 1:
                us01();
                break;
            case 2:
                us02();
                break;
            case 3:
                us03();
                break;
            case 4:
                us04();
                break;
            case 5:
                us05();
                break;
            case 6:
                us06();
                break;
            case 7:
                us07();
                break;
            case 8:
                us08();
                break;
            case 9:
                us09();
                break;
            case 10:
                us10();
                break;
            case 0:
                break;
            default:
                printf("Invalid option\n");
        }
    } while(option != 0);
}