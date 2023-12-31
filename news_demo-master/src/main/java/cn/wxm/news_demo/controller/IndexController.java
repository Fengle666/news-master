package cn.wxm.news_demo.controller;

import cn.wxm.news_demo.domain.ArtImage;
import cn.wxm.news_demo.domain.Article;
import cn.wxm.news_demo.domain.Category;
import cn.wxm.news_demo.service.ArtImageService;
import cn.wxm.news_demo.service.ArticleService;
import cn.wxm.news_demo.service.CateService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.rmi.runtime.Log;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: news_demo
 * @description: 前端主页和一些共有页面控制器
 * @author: ceshi
 * @create: 2021-02-18 13:33
 **/
@Controller
@Slf4j
public class IndexController {
    @Autowired
    ArticleService articleService;

    @Autowired
    ArtImageService imageService;

    @Autowired
    CateService cateService;
    //时间线页面
    @RequestMapping("/time_line")
    public String time_line(Model model) {

        return "time_line";
    }

    //
    @RequestMapping("/tips")
    public String tips(Model model) {

        return "tips";
    }
//    @RequestMapping("/hh")
//    public String common(){
//        return "common";
//    }

//    网站首页
    @RequestMapping( value = {"/index","/"})
    public String index(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,HttpSession session) {

        //查询所有文章信息
        List<Article> articleList = articleService.list();
//        model.addAttribute("list",articleList);
        //使用MP自带的分页插件分页查询数据
        Page<Article> articlePage = new Page<>(pn, 8);
        //分页查询结果
        Page<Article> pages = articleService.page(articlePage, null);
        //所有记录放在record里面
//        List<Article> records = pages.getRecords();
        model.addAttribute("pages",pages);
        model.addAttribute("currPn",pn);
//        List<ArtImage> imageList = imageService.list();
//        model.addAttribute("images",imageList);

        //将分类的cid保存到session中
        List<Category> list = cateService.list();
        session.setAttribute("cateList",list);
        //调用排行榜方法
        articleService.getRowData(session);
        //调用最新评论新闻方法
        List<Article> newComArt = articleService.getNewComArt(session);
        session.setAttribute("newComArt",newComArt);
        //返回标签列表
//        List<Category> categories = cateService.list(null);
//        model.addAttribute("categories",categories);
        return "index";
    }

    //首页列表请求数据
    @RequestMapping("/index/queryAll")
    @ResponseBody
    public Map queryAll(Integer pageNum, Integer pageSize, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        try {
            IPage<Article> page = articleService.findByPage(pageNum, pageSize,session);
            List<Article> addCateList = page.getRecords();
            long total = page.getTotal();
            map.put("data",addCateList);
            map.put("count",total);
            map.put("status",200);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return map;
    }

}
