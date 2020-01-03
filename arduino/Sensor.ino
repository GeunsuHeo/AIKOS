#define IN_VIB 2
#define OUT_LED 3
#define IN_INF 4
#define TRIG 6 //TRIG 핀 설정 (초음파 보내는 핀)
#define ECHO 5 //ECHO 핀 설정 (초음파\받는 핀)
#define PORT_SC 10
#define ON_OFF 7
#define LABEL_A 8
#define LABEL_B 9

#include <SPI.h>      
#include <SD.h>                                       .. 

File myFile;
void setup() {
  Serial.begin(9600);                                                      // 시리얼 통신을 시작, 통신속도는 (9600)
  pinMode(OUT_LED, OUTPUT);
  pinMode(IN_VIB, INPUT);
  pinMode(IN_INF, INPUT);
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
  pinMode(ON_OFF, INPUT);
  pinMode(LABEL_A, INPUT);
  pinMode(LABEL_B, INPUT);
  
  // 여기서부터 SD카드
  Serial.print("Initializing SD card...");
 
  //SD카드 초기화 SD.begin(4) 는  CS핀번호
  while (!SD.begin(PORT_SC)) {
    Serial.println("initialization failed!");
    delay(1000);
  }
  Serial.println("initialization done.");
  //sd카드에 저장
  myFile = SD.open("datalog.txt", FILE_WRITE); 
  if (myFile) {
    Serial.print("Writing to txt...");
    myFile.println("-----------------------------------");    //test.txt 파일에 문자열을 쓴다.
    Serial.println("done.");
  } else {
    // if the file didn't open, print an error:
    Serial.println("error opening txt");
  }
  myFile.close();
}

void loop() {
  int vib; 
  int inf;
  int labelA, labelB, label_value;
  char filename = "20190728.txt";
  long duration, distance;
  int on;
  on = digitalRead(ON_OFF);
  if(on == LOW){
      digitalWrite(TRIG, LOW);
      delayMicroseconds(2);
      digitalWrite(TRIG, HIGH);
      delayMicroseconds(10);
      digitalWrite(TRIG, LOW);
      duration = pulseIn (ECHO, HIGH); //물체에 반사되어돌아온 초음파의 시간을 변수에 저장합니다.
      //34000*초음파가 물체로 부터 반사되어 돌아오는시간 /1000000 / 2(왕복값이아니라 편도값이기때문에 나누기2를 해줍니다.)
     //초음파센서의 거리값이 위 계산값과 동일하게 Cm로 환산되는 계산공식 입니다. 수식이 간단해지도록 적용했습니다.
      distance = duration * 17 / 1000; 
    
      vib = digitalRead(IN_VIB);
      inf = digitalRead(IN_INF);
      labelA = digitalRead(LABEL_A);
      labelB = digitalRead(LABEL_B);
      
      //PC모니터로 초음파 거리값을 확인 하는 코드 입니다.
      //Serial.println("-----------------");
      //Serial.print("duration : ");
      //Serial.println(duration); //초음파가 반사되어 돌아오는 시간을 보여줍니다.
      //Serial.print("Distance : ");
      //Serial.print(distance); //측정된 물체로부터 거리값(cm값)을 보여줍니다.
      //Serial.println(" Cm");
      delay(10);
    
      if(labelA == HIGH){
        label_value = 1;
        }
       else if(labelB == HIGH){
        label_value = 2;
        }
       else{
        label_value = 0;
        }
    
      //그래프 그리기
      Serial.print(distance);
      //Serial.print("\r");
      Serial.print(",");
      Serial.println(label_value);
    
      
      //진동센서
      /*
      if (vib == LOW) {         
        Serial.println("Vibration : Sensing!");  
      }
      else { 
        Serial.println("Vibration : NOT Sensing"); 
      }
      */
      delay(10);
      if (inf == HIGH){
          //Serial.println("infrare   : Sensing!");
          digitalWrite(OUT_LED, HIGH);
      }
      else {
          //Serial.println("infrare   : NOT Sensing!");  
          digitalWrite(OUT_LED, LOW);
      }
      //sd카드에 저장
      myFile = SD.open("datalog.txt", FILE_WRITE); 
      if (myFile) {
        //Serial.print("Writing to txt...");
        myFile.print(duration);    //test.txt 파일에 문자열을 쓴다.
        myFile.print(",");
        myFile.println(label_value);
        //Serial.println("done.");
      } else {
        // if the file didn't open, print an error:
        //Serial.println("error opening txt");
      }
      myFile.close();
      delay(100);
    }
    else{
      Serial.println("OFF");
      delay(1000);
      }
}
