package com.zjtt.util;

import com.zjtt.pojo.TbSpecification;
import com.zjtt.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

public class Specfication implements Serializable {
    private TbSpecification tbSpecification;
    private List<TbSpecificationOption> spectionOptionList;

    public TbSpecification getTbSpecification() {
        return tbSpecification;
    }

    public void setTbSpecification(TbSpecification tbSpecification) {
        this.tbSpecification = tbSpecification;
    }

    public List<TbSpecificationOption> getSpectionOptionList() {
        return spectionOptionList;
    }

    public void setSpectionOptionList(List<TbSpecificationOption> spectionOptionList) {
        this.spectionOptionList = spectionOptionList;
    }
}
