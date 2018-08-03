package yuni.library.satellite;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class FakeClock {

    public long now = 0;

    public void walk(long time) {
        now += time;
    }

    public Answer<Long> asAnswer() {
        return new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                return now;
            }
        };
    }

}
