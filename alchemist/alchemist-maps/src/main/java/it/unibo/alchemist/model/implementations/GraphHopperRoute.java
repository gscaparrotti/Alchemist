/*
 * Copyright (C) 2010-2015, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.model.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.graphhopper.GHResponse;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.PointList;

import it.unibo.alchemist.model.implementations.positions.LatLongPosition;
import it.unibo.alchemist.model.interfaces.Route;
import it.unibo.alchemist.model.interfaces.Position;
import java.util.stream.Collectors;

/**
 */
public final class GraphHopperRoute implements Route {

    private static final long serialVersionUID = -1455332156736222268L;
    private int size;
    private double distance, time;
    private List<Position> points;

    /**
     * @param response
     *            the response to use
     */
    public GraphHopperRoute(final GHResponse response) {
        final List<GHResponse> responseList = new ArrayList<>(1);
        responseList.add(response);
        new GraphHopperRoute(responseList);
    }

    /**
     * @param paths A List of paths which will be concatenated creating a single path.
     * Please note that the concatenation order is the list's one.
     */
    public GraphHopperRoute(final List<GHResponse> paths) {
        final Set<Throwable> errors = paths.stream().flatMap(p -> p.getErrors().stream()).collect(Collectors.toSet());
        if (errors.isEmpty()) {
            final ArrayList<PathWrapper> resps = new ArrayList<>(paths.size());
            for (int i = 0; i < paths.size(); i++) {
                final PathWrapper resp = paths.get(i).getBest();
                resps.add(resp);
                time += resp.getTime() / 1000d;
                distance += resp.getDistance();
                final PointList pts = resp.getPoints();
                size += pts.getSize();
                final List<Position> temp = new ArrayList<>(size);
                for (int a = 0; a < pts.getSize(); a++) {
                    temp.add(new LatLongPosition(pts.getLatitude(a), pts.getLongitude(a)));
                }
                if (points == null) {
                    points = new ArrayList<>(temp.size());
                }
                points.addAll(temp);
            }
        } else {
            final String msg = errors.stream().map(Throwable::getMessage).collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(msg, errors.stream().findAny().orElse(new Throwable("(unknown")));
        }
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public Position getPoint(final int step) {
        return points.get(step);
    }

    @Override
    public List<Position> getPoints() {
        return points;
    }

    @Override
    public int getPointsNumber() {
        return size;
    }

    @Override
    public double getTime() {
        return time;
    }

}
