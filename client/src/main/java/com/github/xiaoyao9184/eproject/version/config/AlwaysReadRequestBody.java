package com.github.xiaoyao9184.eproject.version.config;

import java.util.function.Predicate;

/**
 * Created by xy on 2021/2/3.
 */
public class AlwaysReadRequestBody implements Predicate {

    @Override
    public boolean test(Object o){
        return true;
    }

}
