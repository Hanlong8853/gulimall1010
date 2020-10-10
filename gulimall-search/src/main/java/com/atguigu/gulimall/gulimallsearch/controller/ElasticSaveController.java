package com.atguigu.gulimall.gulimallsearch.controller;

import com.atguigu.common.es.SkuEsModel;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.gulimallsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author: Hanlong
 * @description: some desc
 * @date: 2020/10/10 5:03 下午
 */
@Slf4j
@RequestMapping(value = "/search/save")
@RestController
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;


    /**
     * 上架商品
     * @param skuEsModels
     * @return
     */
    @PostMapping(value = "/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {

        boolean status=false;
        try {
            status = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            //log.error("商品上架错误{}",e);

            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_UP_EXCEPTION.getMessage());
        }

        if(status){
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_UP_EXCEPTION.getMessage());
        }else {
            return R.ok();
        }

    }


}