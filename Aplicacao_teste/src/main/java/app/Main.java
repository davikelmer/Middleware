package app;


import flash.start.*;

public class Main {
    public static void main(String[] args) {
        Flash.register(new TarefaController());
        Flash.start(8080);
    }
}

