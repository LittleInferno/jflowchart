package com.littleinferno.flowchart.codegen;

import com.littleinferno.flowchart.pin.Pin;

public interface CodeGen {
    String gen(CodeBuilder builder, Pin with);
}