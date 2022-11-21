package za.nmu.wrr.controllers;

import za.nmu.wrr.circle.CircleView;

@FunctionalInterface
public interface Run {
    void run(CircleView cv);
}
