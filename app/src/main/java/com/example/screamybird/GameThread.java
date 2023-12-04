//package com.example.screamybird;
//
//public class GameThread {
//    private Game game;
//    private boolean running;
//    private int fr; //frame rate
//    public GameThread (Game game) {
//        this.game = game;
//    }
//
//
//    /*
//    * This will probably change
//    * if u need to change anything here go ahead
//    */
//    public void run() {
//        long lastTime = System.currentTimeMillis();
//
//        // Game loop
//        while (running) {
//            long now = System.currentTimeMillis();
//            long elapsed = now - lastTime;
//
//            if (elapsed < fr) {
//                game.update(elapsed); //need to add update function to game
//                game.draw(); //need to add draw
//            }
//            lastTime = now;
//        }
//    }
//
//    public void shutdown() {
//        running = false;
//    }
//}

