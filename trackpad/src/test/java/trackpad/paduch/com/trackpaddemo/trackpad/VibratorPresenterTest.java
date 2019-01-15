package trackpad.paduch.com.trackpaddemo.trackpad;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import trackpad.paduch.com.trackpaddemo.trackpad.contract.VibratorContract;
import trackpad.paduch.com.trackpaddemo.trackpad.data.CoordinatesDao;
import trackpad.paduch.com.trackpaddemo.trackpad.presenter.VibratorPresenter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

@RunWith(MockitoJUnitRunner.class)
public class VibratorPresenterTest {
    @Mock
    private VibratorContract.View view;

    @Mock
    private CoordinatesDao dao;

    @InjectMocks
    private VibratorPresenter presenter;

    @Test
    public void initTest() {
        float returnVal = 0.1f;
        when(dao.getX()).thenReturn(returnVal);
        when(dao.getY()).thenReturn(returnVal);
        presenter.init();
        verify(dao, atLeastOnce()).getX();
        verify(dao, atLeastOnce()).getY();
        verify(view).restoreCoordinates(returnVal, returnVal);
        verify(view).updateCoordinatesText(returnVal, returnVal);
    }

    @Test
    public void saveCoordsTest() {
        presenter.saveCoordinates(1, 1);
        verify(dao).saveY(1);
        verify(dao).saveX(1);
    }

    @Test
    public void onTrackPadChangeHasVibratorTest() {
        when(view.checkVibrator()).thenReturn(true);
        presenter.onTrackPadChange(2, 1);
        verify(view).vibrate(2 * VibratorPresenter.TIME_MULTIPLIER, VibratorPresenter.MAX_AMPLITUDE);
        verify(view).updateCoordinatesText(2, 1);
        verify(view, never()).onError(anyString());

    }

    @Test
    public void onTrackPadChangeNoVibratorTest() {
        when(view.checkVibrator()).thenReturn(false);
        presenter.onTrackPadChange(2, 1);
        verify(view, never()).vibrate(2 * VibratorPresenter.TIME_MULTIPLIER, VibratorPresenter.MAX_AMPLITUDE);
        verify(view, never()).updateCoordinatesText(2, 1);
        verify(view).onError(anyString());
    }
}
