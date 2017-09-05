package com.novoda.tpbot.human;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.novoda.notils.caster.Views;
import com.novoda.support.SelfDestructingMessageView;
import com.novoda.support.SwitchableView;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;
import com.novoda.tpbot.ServerDeclarationListener;
import com.novoda.tpbot.controls.CommandRepeater;
import com.novoda.tpbot.controls.ControllerListener;
import com.novoda.tpbot.controls.ControllerView;
import com.novoda.tpbot.controls.LastServerPersistence;
import com.novoda.tpbot.controls.LastServerPreferences;
import com.novoda.tpbot.controls.ServerDeclarationView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class HumanActivity extends AppCompatActivity implements HumanView {

    private static final String LAZERS = String.valueOf(Character.toChars(0x1F4A5));

    private SelfDestructingMessageView debugView;
    private SwitchableView switchableView;
    private CommandRepeater commandRepeater;

    @Inject
    HumanTelepresenceService humanTelepresenceService;

    private HumanPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        setContentView(R.layout.activity_human);

        debugView = Views.findById(this, R.id.bot_controller_debug_view);
        switchableView = Views.findById(this, R.id.bot_switchable_view);

        LastServerPersistence lastServerPersistence = LastServerPreferences.newInstance(this);
        presenter = new HumanPresenter(humanTelepresenceService, this, lastServerPersistence);

        Handler handler = new Handler();
        commandRepeater = new CommandRepeater(commandRepeatedListener, handler);

        ControllerView controllerView = Views.findById(switchableView, R.id.bot_controller_direction_view);
        controllerView.setControllerListener(controllerListener);

        ServerDeclarationView serverDeclarationView = Views.findById(switchableView, R.id.bot_server_declaration_view);
        serverDeclarationView.setServerDeclarationListener(serverDeclarationListener);

    }

    private final CommandRepeater.Listener commandRepeatedListener = new CommandRepeater.Listener() {
        @Override
        public void onCommandRepeated(String command) {
            debugView.showTimed(command);
            Direction direction = Direction.from(command);
            presenter.moveIn(direction);
        }
    };

    private final ControllerListener controllerListener = new ControllerListener() {

        @Override
        public void onDirectionPressed(Direction direction) {
            commandRepeater.startRepeatingCommand(direction.rawDirection());
        }

        @Override
        public void onDirectionReleased(Direction direction) {
            commandRepeater.stopRepeatingCommand(direction.rawDirection());
        }

        @Override
        public void onLazersFired() {
            // no-op for debug
        }

        @Override
        public void onLazersReleased() {
            // no-op for debug
        }
    };

    private final ServerDeclarationListener serverDeclarationListener = new ServerDeclarationListener() {
        @Override
        public void onConnect(String serverAddress) {
            debugView.showPermanently(getString(R.string.connecting_ellipsis));
            presenter.startPresenting(serverAddress);
        }
    };

    @Override
    public void onConnect(String message) {
        debugView.showPermanently(getString(R.string.connected));
        switchableView.setDisplayedChild(1);
    }

    @Override
    public void onDisconnect() {
        debugView.showPermanently(getString(R.string.disconnected));
        switchableView.setDisplayedChild(0);
    }

    @Override
    public void onError(String message) {
        debugView.showPermanently(message);
        switchableView.setDisplayedChild(0);
    }

    @Override
    protected void onPause() {
        commandRepeater.stopCurrentRepeatingCommand();
        super.onPause();
    }

    @Override
    protected void onStop() {
        presenter.stopPresenting();
        super.onStop();
    }

}
