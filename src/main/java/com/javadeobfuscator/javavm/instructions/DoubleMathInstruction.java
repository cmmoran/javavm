package com.javadeobfuscator.javavm.instructions;

import com.javadeobfuscator.javavm.Locals;
import com.javadeobfuscator.javavm.MethodExecution;
import com.javadeobfuscator.javavm.Stack;
import com.javadeobfuscator.javavm.exceptions.ExecutionException;
import com.javadeobfuscator.javavm.utils.BiDoubleFunction;
import com.javadeobfuscator.javavm.utils.ExecutionUtils;
import com.javadeobfuscator.javavm.values.JavaUnknown;
import com.javadeobfuscator.javavm.values.JavaValue;
import com.javadeobfuscator.javavm.values.JavaValueType;
import com.javadeobfuscator.javavm.values.JavaWrapper;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.List;

public class DoubleMathInstruction extends Instruction {
    private final BiDoubleFunction _function;
    private final boolean _returnInt;

    public DoubleMathInstruction(BiDoubleFunction function, boolean returnInt) {
        this._function = function;
        this._returnInt = returnInt;
    }

    @Override
    public void execute(MethodExecution execution, AbstractInsnNode currentInsn, Stack stack, Locals locals, List<AbstractInsnNode> branchTo) {
        JavaValue b = stack.pop().get();
        JavaValue a = stack.pop().get();

        if (ExecutionUtils.areValuesUnknown(a, b)) {
            stack.push(JavaWrapper.wrap(new JavaUnknown(execution.getVM(), execution.getVM().DOUBLE, JavaUnknown.UnknownCause.DOUBLE_MATH, b, a)));
            return;
        }

        if (!a.is(JavaValueType.DOUBLE) || !b.is(JavaValueType.DOUBLE)) {
            throw new ExecutionException("Expected to find double on stack");
        }

        double result = _function.apply(a.asDouble(), b.asDouble());
        if (!_returnInt) {
            stack.push(JavaWrapper.createDouble(execution.getVM(), result));
        } else {
            stack.push(JavaWrapper.createInteger(execution.getVM(), (int) result));
        }
    }
}
