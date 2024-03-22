package com.example.categoryserver.service;

import com.example.categoryserver.models.Order;
import com.example.categoryserver.models.User;
import com.example.categoryserver.repositories.OrderRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
//    private final RestTemplate restTemplate;

    RestTemplate restTemplate = new RestTemplate();

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
//        this.restTemplate = restTemplate;
    }

    @Retry(name = "retryApi")
    public List<Order> getListOrder(){
        List<Order> orderList = orderRepository.findAll();
        for (Order o: orderList) {
            User user = restTemplate.getForObject("http://localhost:8080/api/v1/users/"+o.getId(), User.class);
            o.setUser(user);
        }
        return orderList;
    }
    public Order getOrderById(long id){
        Order order = orderRepository.findById(id).get();
        User user = restTemplate.getForObject("http://localhost:8080/api/v1/users/"+id, User.class);
        order.setUser(user);
        return order;
    }

    public Order addOrder(Order order){
        return orderRepository.save(order);
    }

    public void deleteOrderById(long id){
        orderRepository.deleteById(id);
    }
}
