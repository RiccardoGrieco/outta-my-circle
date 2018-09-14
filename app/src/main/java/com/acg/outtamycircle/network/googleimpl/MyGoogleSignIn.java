package com.acg.outtamycircle.network.googleimpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.acg.outtamycircle.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MyGoogleSignIn {
    private static final GoogleSignInOptions OPTIONS = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
    private GoogleSignInClient client;
    private final Activity activity;
    private String playerId;

    public String getPlayerId() {
        return playerId;
    }

    public MyGoogleSignIn(Activity activity) {
        this.activity = activity;
        this.client = GoogleSignIn.getClient(activity, OPTIONS);
    }

    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(activity) != null;
    }

    public GoogleSignInAccount getAccount() {
        return GoogleSignIn.getLastSignedInAccount(activity);
    }

    public void signOut() {
        client.signOut();
    }

    private void onConnected(GoogleSignInAccount account) {

        PlayersClient playersClient = Games.getPlayersClient(activity, account);
        playersClient.getCurrentPlayer()
                .addOnSuccessListener(new OnSuccessListener<Player>() {
                    @Override
                    public void onSuccess(Player player) {
                        playerId = player.getPlayerId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        playerId = null;
                    }
                });

    }

    private void onDisconnected() {
        signOut();
        //TODO other stuff
    }

    public void signIn() { //onresume
        client.silentSignIn().addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                if (!task.isSuccessful())
                    explicitSignIn();
                else
                    onConnected(task.getResult());
            }
        });
    }
    private void explicitSignIn() {
        Intent signInIntent = client.getSignInIntent();
        activity.startActivityForResult(signInIntent, GoogleRC.RC_SIGN_IN);
    }
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GoogleRC.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                onConnected(task.getResult(ApiException.class));
            } catch (ApiException apiException) {
                onDisconnected();
                new AlertDialog.Builder(activity)
                        .setMessage(activity.getString(R.string.google_error_signin) + " (" + apiException.getStatusCode() + ")")
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        }
    }
}
