package edu.vandy.simulator.managers.beings.executorService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import edu.vandy.simulator.ReflectionHelper;
import edu.vandy.simulator.managers.beings.BeingManager;
import edu.vandy.simulator.managers.palantiri.Palantir;
import edu.vandy.simulator.utils.Assignment;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Assignment_2A_BeingCallableTest {

    @Mock
    BeingManager mockBeingManager = mock(ExecutorServiceMgr.class);

    @Mock
    Palantir mockPalantir = mock(Palantir.class);

    @Mock
    BeingCallable mockBeingCallable = mock(BeingCallable.class);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void before() {
        if (Assignment.isUndergraduateTodo() && Assignment.isGraduateTodo()) {
            System.out.println("Running UNDERGRADUATE and GRADUATE tests ...");
        } else if (Assignment.isUndergraduateTodo()) {
            System.out.println("Running UNDERGRADUATE tests ...");
        } else if (Assignment.isGraduateTodo()) {
            System.out.println("Running GRADUATE tests ...");
        } else {
            throw new IllegalStateException("Assignment.sTypes value is not valid: " + Assignment.sTypes);
        }
    }

    @Test(timeout = 4000)
    public void call() {
        BeingCallable beingCallable = new BeingCallable(mockBeingManager);

        // Make the SUT call.
        BeingCallable callResult = beingCallable.call();

        // Check Results.
        assertNotNull("Call should not return null.", callResult);
        assertSame("Call should return the BeingCallable instance.", beingCallable, callResult);
    }

    @Test(timeout = 4000)
    public void testAcquirePalantirAndGazeMethod() {
        BeingCallable being = new BeingCallable(mockBeingManager);
        assertNotNull("new BeingCallable should not be null.", being);

        when(mockBeingManager.acquirePalantir(same(being))).thenReturn(mockPalantir);
        doNothing().when(mockBeingManager).releasePalantir(same(being), same(mockPalantir));

        InOrder inOrder = inOrder(mockBeingManager, mockPalantir);

        // Make the SUT call.
        being.acquirePalantirAndGaze();

        verify(mockBeingManager, times(1)).acquirePalantir(being);
        verify(mockBeingManager, times(1)).releasePalantir(being, mockPalantir);
        verify(mockBeingManager, never()).error(anyString());

        inOrder.verify(mockBeingManager).acquirePalantir(being);
        inOrder.verify(mockPalantir).gaze(being);
        inOrder.verify(mockBeingManager).releasePalantir(being, mockPalantir);
    }

    @Test(timeout = 4000)
    public void testAcquirePalantirAndGazeMethodErrorHandling() {
        BeingManager beingManager = mock(ExecutorServiceMgr.class);

        BeingCallable being = new BeingCallable(beingManager);
        assertNotNull("new BeingCallable should not be null.", being);

        when(beingManager.acquirePalantir(same(being))).thenReturn(null);
        doNothing().when(beingManager).releasePalantir(any(), any());
        doNothing().when(beingManager).error(anyString());

        InOrder inOrder = inOrder(beingManager);

        // Make the SUT call.
        being.acquirePalantirAndGaze();

        verify(beingManager, times(1)).acquirePalantir(being);
        verify(beingManager, times(1)).error(anyString());
        verify(beingManager, never()).releasePalantir(any(), any());

        inOrder.verify(beingManager).acquirePalantir(being);
        inOrder.verify(beingManager).error(anyString());
    }
}
