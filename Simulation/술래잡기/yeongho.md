```java
import java.util.*;
import java.math.*;
import java.io.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;

    static int N,M,H,K;
    static int[][] board;

    static List<Person> personLs = new ArrayList<>();
    static Person2 p = new Person2();
       

    /*
    00 01 02
    10 11 12
    20 21 22
    */
    // 하, 상, 우, 좌
    static int[] pdx = {1,-1,0,0};
    static int[] pdy = {0,0,1,-1};

    static class Person{
        int x,y;
        int dir;
        int type;

        boolean die = false;

        void setDie(){
            this.die = true;
        }

        Person(int x, int y, int type){
            this.x = x;
            this.y = y;
            this.type = type;
        }

        // 초기 이동 방향 세팅
        void setType(){
            if(this.type == 1){
                this.dir = 2;
            }else{
                this.dir = 0;
            }
        }

        // 벽 만나면 반대 방향으로
        void checkWall(){
            if(this.type == 1){
                if(this.dir == 3){
                    this.dir = 2;
                }else{
                    this.dir = 3;
                }
            }else{
                if(this.dir == 0){
                    this.dir = 1;
                }else{
                    this.dir = 0;
                }
            }
        }

        // 이동
        void move(){
            int nx = x + pdx[dir];
            int ny = y + pdy[dir];
            
            if(nx == p.x && ny == p.y) return;

            if(nx < 0 || nx >= N || ny < 0 || ny >= N){
                checkWall();
                // 벽 돌고 다시 움직여야함
                nx = x + pdx[dir];
                ny = y + pdy[dir];
                
                if(nx == p.x && ny == p.y) return;
            }


            x = nx;
            y = ny;
        }

        
    }

    static class Pair{
        int x, y;

        Pair(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    /*
    00 01 02
    10 11 12
    20 21 22
    */
    static int[] dx = {-1,0,1,0};
    static int[] dy = {0,1,0,-1};

    /*
    00 01 02
    10 11 12
    20 21 22
    */
    static int[] rdx = {1,0,-1,0};
    static int[] rdy = {0,1,0,-1};


    static class Person2{
        int x, y; // 술래 좌표
        int dir = 0; // 이동 방향
        int idx = 0; // 움직임 인덱스
        int remain; // 움직임 남은 숫자
        boolean type = true; // 정방향 = true or 역방향 = false
        List<Integer> load = new ArrayList<>();
        List<Integer> rload = new ArrayList<>();
        
        int ans = 0;

        void init(){
            this.x = Math.round(N/2);
            this.y = Math.round(N/2);

            /*
            1, 1, 2, 2, 3, 3, 4, 4, 4
            
            그럼 방향이랑 순서를 어디에 저장을 해둬야함
            
            */

            for(int i = 1; i < N; i++){
                load.add(i);
                load.add(i);
            }
            load.add(N-1);

            remain = load.get(0);

            for(int i =load.size()-1; i >= 0; i--){
                rload.add(load.get(i));
            }
        }

        void setX(int x){
            this.x = x;
        }

        void setY(int y){
            this.y = y;
        }

        void move(){
            if(type){
                // 정방향
                forward();
            }else{
                // 역방향
                reverse();
            }
        }

        void forward(){

            this.x += dx[dir];
            this.y += dy[dir];

            remain--;
            
            if(remain == 0){
                
                dir++;

                if (dir >= 4){
                    dir = 0;
                }
                
                idx++;

                if(idx < load.size()){
                    remain = load.get(idx);
                }
            }

            if(x == 0 && y == 0){
                type = false;
                idx = 0;
                dir = 0;
                remain = rload.get(0);
            }
        }

        // 상 우 하 좌
        void reverse(){
            this.x += rdx[dir];
            this.y += rdy[dir];

            remain--;
            
            if(remain == 0){
                dir++;

                if (dir >= 4){
                    dir = 0;
                }
                
                idx++;

                if(idx < rload.size()){
                    remain = rload.get(idx);
                }

            }

            int center = Math.round(N/2);

            if(x == center && y == center){
                type = true;
                idx = 0;
                dir = 0;
                remain = 1;
            }


        }

        void see(int num){

            int result = 0;
            int nx = 0;
            int ny = 0;
            

            if(type){
                
                int tx = x;
                int ty = y;
                
                for(int i = 0; i < 3; i++){

                    if(i == 0){
                        nx = tx;
                        ny = ty;
                    }else{
                        nx = tx + dx[dir];
                        ny = ty + dy[dir];
                    }

                    if(nx < 0 || nx >= N || ny < 0 || ny >= N) continue;

                    if(board[nx][ny] == 1){
                        // 나무 있는 곳에 도망자 있으면 안잡음
                        tx = nx;
                        ty = ny;
                        continue;
                    }

                    for(int k = 0; k < personLs.size(); k++){
                        Person pp = personLs.get(k);
                        if(pp.x == nx && pp.y == ny && pp.die == false){
                            pp.setDie();
                            result++;
                        }
                    }


                    tx = nx;
                    ty = ny;
                    
                }

                

            }else{

                int tx = x;
                int ty = y;

                for(int i = 0; i < 3; i++){
                    
                    if(i == 0){
                        nx = tx;
                        ny = ty;
                    }else{
                        nx = tx + rdx[dir];
                        ny = ty + rdy[dir];
                    }

                    if(nx < 0 || nx >= N || ny < 0 || ny >= N) continue;

                    if(board[nx][ny] == 1){
                        // 나무 있는 곳에 도망자 있으면 안잡음
                        tx = nx;
                        ty = ny;
                        continue;
                    }

                    for(int k = 0; k < personLs.size(); k++){
                        Person pp = personLs.get(k);
                        if(pp.x == nx && pp.y == ny && pp.die == false){
                            pp.setDie();
                            result++;
                        }
                    }


                    tx = nx;
                    ty = ny;
                    
                }

            }

            ans += result * (num+1);
        }
        
    }

    static int calc(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static void main(String[] args) throws IOException {
        // Please write your code here.
        input();
        p.init();
        
        // for(int j =0; j < personLs.size(); j++){
        //     System.out.println(personLs.get(j).x + " " + personLs.get(j).y + " " + personLs.get(j).die);
        // }
        // System.out.println();
        
        for(int i =0; i < K; i++){
            // 도망자 움직임
            for(int k =0; k < personLs.size(); k++){
                if(calc(p.x, p.y, personLs.get(k).x, personLs.get(k).y) <= 3){
                    if(personLs.get(k).die == true) continue;
                    personLs.get(k).move();
                }
            }

            // 술래 움직임
            p.move();
            
            p.see(i);

            // for(int j =0; j < personLs.size(); j++){
            //     System.out.println(personLs.get(j).x + " " + personLs.get(j).y + " " + personLs.get(j).die);
            // }
            // System.out.println();
        }

        // for(int i =0; i < personLs.size(); i++){
        //     System.out.println(personLs.get(i).x + " " + personLs.get(i).y + " " + personLs.get(i).die);
        // }

        System.out.println(p.ans);

                
    }

    static void input() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        H = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        board = new int[N][N];

        for(int i =0; i < M; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken())-1;
            int y = Integer.parseInt(st.nextToken())-1;
            int type = Integer.parseInt(st.nextToken());
            

            personLs.add(new Person(x,y,type));
            personLs.get(i).setType();
        }

        for(int i =0; i < H; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            board[x-1][y-1] = 1; 
        }
    }
}
```
