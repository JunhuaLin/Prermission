package cn.junhua.android.permissionagent;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.junhua.android.permission.PermissionAgent;
import cn.junhua.android.permission.agent.AgentExecutor;
import cn.junhua.android.permission.agent.callback.OnDeniedCallback;
import cn.junhua.android.permission.agent.callback.OnGrantedCallback;
import cn.junhua.android.permission.agent.callback.OnRationaleCallback;
import cn.junhua.android.permission.special.SpecialPermission;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_start_setting_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionAgent.getInstance().startSettingPage(0x123);
            }
        });
        findViewById(R.id.tv_check_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testCheckPermission(v);
            }
        });

        findViewById(R.id.tv_cwc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_CONTACTS);
            }
        });
        findViewById(R.id.tv_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(Manifest.permission_group.LOCATION);
            }
        });
        findViewById(R.id.tv_serial_requests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestEachPermission(Manifest.permission_group.CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        });
        findViewById(R.id.tv_amws).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOverlay(v);
            }
        });
        findViewById(R.id.tv_ws).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWriteSettings(v);
            }
        });
        findViewById(R.id.tv_amuas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestInstallApk(v);

            }
        });
        findViewById(R.id.tv_nofity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNotify(v);
            }
        });
    }

    private void testCheckPermission(View v) {
        String stringBuilder = "ACCESS_FINE_LOCATION:" +
                PermissionAgent.getInstance().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) +
                "\n" +
                "CALL_PHONE,CAMERA:" +
                PermissionAgent.getInstance().checkPermission(Manifest.permission.CALL_PHONE,
                        Manifest.permission.CAMERA) +
                "\n" +
                "ACCESS_NOTIFICATION_POLICY:" +
                PermissionAgent.getInstance().checkPermission(SpecialPermission.ACCESS_NOTIFICATION_POLICY);
        toast(stringBuilder);
    }

    private void requestNotify(View v) {
        PermissionAgent.getInstance()
                .request(SpecialPermission.ACCESS_NOTIFICATION_POLICY)
                .onGranted(new OnGrantedCallback<SpecialPermission>() {
                    @Override
                    public void onGranted(Context context, SpecialPermission permissions) {
                        toast("onGranted() called with: permissions = [" + permissions + "]");
                    }
                })
                .onDenied(new OnDeniedCallback<SpecialPermission>() {
                    @Override
                    public void onDenied(Context context, SpecialPermission permissions) {
                        toast("onDenied() called with: permissions = [" + permissions + "]");
                    }
                })
                .apply();
    }


    public void requestPermission(String... permissions) {
        PermissionAgent.getInstance()
                .request(permissions)
                //.code(123)//与你自定义code冲突时可以设置，一般不用自己设置
                .onGranted(new OnGrantedCallback<List<String>>() {
                    @Override
                    public void onGranted(Context context, List<String> permissions) {
                        toast("onGranted() called with: permissions = [" + permissions + "]");
                        Log.d(TAG, "onGranted() called with: permissions = [" + permissions + "]");
                    }
                })
                .onDenied(new OnDeniedCallback<List<String>>() {
                    @Override
                    public void onDenied(Context context, List<String> permissions) {
                        toast("onDenied() called with: permissions = [" + permissions + "]");
                        Log.d(TAG, "onDenied() called with: permissions = [" + permissions + "]");

                        List<String> pList = new ArrayList<>();
                        for (String p : permissions) {
                            if (PermissionAgent.getInstance().hasAlwaysDeniedPermission(p)) {
                                pList.add(p);
                            }
                        }
                        if (!pList.isEmpty()) {
                            startSettingPageDialog(pList);
                        }
                    }
                })
                .onRationale(new OnRationaleCallback<List<String>>() {
                    @Override
                    public void onRationale(Context context, List<String> permissions, AgentExecutor executor) {
                        toast("onRationale() called with: permissions = [" + permissions + "], executor = [" + executor + "]");
                        Log.d(TAG, "onRationale() called with: permissions = [" + permissions + "], executor = [" + executor + "]");
                        showRationaleDialog(permissions, executor);
                    }
                })
                .apply();
    }

    private void requestEachPermission(String... permissions) {
        PermissionAgent.getInstance()
                .requestEach(permissions)
                .onGranted(new OnGrantedCallback<List<String>>() {
                    @Override
                    public void onGranted(Context context, List<String> permissions) {
                        toast("onGranted() called with: permissions = [" + permissions + "]");
                        Log.d(TAG, "onGranted() called with: permissions = [" + permissions + "]");
                    }
                })
                .onDenied(new OnDeniedCallback<List<String>>() {
                    @Override
                    public void onDenied(Context context, List<String> permissions) {
                        toast("onDenied() called with: permissions = [" + permissions + "]");
                        Log.d(TAG, "onDenied() called with: permissions = [" + permissions + "]");
                        if (PermissionAgent.getInstance().hasAlwaysDeniedPermission(permissions)) {
                            toast(permissions);
                        }
                    }
                })
                .apply();
    }

    public void requestOverlay(View view) {
        PermissionAgent.getInstance()
                .request(SpecialPermission.SYSTEM_ALERT_WINDOW)
                .onGranted(new OnGrantedCallback<SpecialPermission>() {
                    @Override
                    public void onGranted(Context context, SpecialPermission permissions) {
                        toast("onGranted() called with: permissions = [" + permissions + "]");
                    }
                })
                .onDenied(new OnDeniedCallback<SpecialPermission>() {
                    @Override
                    public void onDenied(Context context, SpecialPermission permissions) {
                        toast("onDenied() called with: permissions = [" + permissions + "]");
                    }
                })
                .apply();
    }


    private void requestWriteSettings(View v) {
        PermissionAgent.getInstance()
                .request(SpecialPermission.WRITE_SETTINGS)
                .onGranted(new OnGrantedCallback<SpecialPermission>() {
                    @Override
                    public void onGranted(Context context, SpecialPermission permissions) {
                        toast("onGranted() called with: permissions = [" + permissions + "]");
                    }
                })
                .onDenied(new OnDeniedCallback<SpecialPermission>() {
                    @Override
                    public void onDenied(Context context, SpecialPermission permissions) {
                        toast("onDenied() called with: permissions = [" + permissions + "]");
                    }
                })
                .apply();
    }


    private void requestInstallApk(View v) {
        PermissionAgent.getInstance()
                .request(SpecialPermission.REQUEST_INSTALL_PACKAGES)
                .code(123)//与你自定义code冲突时可以设置，一般不用自己设置
                .onGranted(new OnGrantedCallback<SpecialPermission>() {
                    @Override
                    public void onGranted(Context context, SpecialPermission permissions) {
                        toast("onGranted() called with: permissions = [" + permissions + "]");
                    }
                })
                .onDenied(new OnDeniedCallback<SpecialPermission>() {
                    @Override
                    public void onDenied(Context context, SpecialPermission permissions) {
                        toast("onDenied() called with: permissions = [" + permissions + "]");
                    }
                })
                .apply();
    }

    private void showRationaleDialog(List<String> permissions, final AgentExecutor agentExecutor) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("同意如下权限来继续运行程序：\n" + TextUtils.join("\n", permissions))
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        agentExecutor.cancel();
                    }
                })
                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        agentExecutor.execute();
                    }
                })
                .show();
    }

    private void startSettingPageDialog(List<String> permissions) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("请在设置中允许如下权限：\n" + TextUtils.join("\n", permissions))
                .setCancelable(true)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionAgent.getInstance().startSettingPage(0x123);
                    }
                })
                .show();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void toast(List<String> permissions) {
        Toast.makeText(this, "请在设置中允许如下权限：\n" + TextUtils.join("\n", permissions), Toast.LENGTH_SHORT).show();
    }
}
