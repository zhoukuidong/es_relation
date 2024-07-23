package com.zkd.demo.dict.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zkd.demo.dict.config.DictFormatSerializerModifier;

public class BootPlusModule extends SimpleModule {

    private DictFormatSerializerModifier dictFormatSerializerModifier;

    public BootPlusModule(DictFormatSerializerModifier dictFormatSerializerModifier) {
        this.dictFormatSerializerModifier = dictFormatSerializerModifier;
    }


    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanSerializerModifier(dictFormatSerializerModifier);
    }

}