package pl.tysia.martech.Persistance;

import java.util.ArrayList;

import pl.tysia.martech.BusinessLogic.Domain.Ware;
import pl.tysia.martech.BusinessLogic.Domain.Order;

public class MockWaresDAO {
    public static ArrayList<Order> getWares(){
        ArrayList<Order> wares = new ArrayList<>();
        wares.add(new Order(new Ware("Przykładowy towar 1")));
        wares.add(new Order(new Ware("Przykładowy towar 2")));

        return wares;
    }
}
