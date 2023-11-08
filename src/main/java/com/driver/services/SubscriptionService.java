package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        Subscription subscription = new Subscription();
        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int noOfScreen = subscriptionEntryDto.getNoOfScreensRequired();

        subscription.setSubscriptionType(subscriptionType);
        subscription.setNoOfScreensSubscribed(noOfScreen);

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        subscription.setUser(user);

        int priceOfSubscription = 0;

        if(subscriptionType.equals(SubscriptionType.BASIC)){
            priceOfSubscription = 500 + (200*noOfScreen);
        }else if(subscriptionType.equals(SubscriptionType.PRO)){
            priceOfSubscription = 800 + (250*noOfScreen);
        }else {
            priceOfSubscription = 1000 + (350*noOfScreen);
        }
        subscription.setTotalAmountPaid(priceOfSubscription);
        subscription.setStartSubscriptionDate(new Date());

        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }

        Integer previousFair = subscription.getTotalAmountPaid();
        Integer currentFair = 0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            currentFair = previousFair + 300 + (50*subscription.getNoOfScreensSubscribed());
        }else{
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            currentFair = previousFair + 200 + (100*subscription.getNoOfScreensSubscribed());
        }
        subscription.setTotalAmountPaid(currentFair);
        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);
        return currentFair - previousFair;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        Integer totalRevenue = 0;
        for(Subscription subscription : subscriptionList){
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
