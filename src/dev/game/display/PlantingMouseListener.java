package dev.game.display;

import dev.game.Game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PlantingMouseListener implements MouseListener {

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        if(Display.getState()){
            Game.addPlant(e.getX(), e.getY());

        }

    }
}
