package zomifi.op27no2.socialbranding;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clover.sdk.util.Platform;

import java.util.ArrayList;

/**
 * Created by Joshua on 9/25/2014.
 */
public class EasyAdapter extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<String> textlist;
    private ArrayList<Boolean> checklist;
    private final TypedArray typedArray;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edt;
    private ViewHolder viewHolder;
    private int total = 0;
    private int chars = 0;

    static class ViewHolder {
        public CheckBox check;
        public ImageView image;
        public TextView text;
        public ImageButton button;
    }

    public EasyAdapter(Context context, ArrayList<String> textlist, ArrayList<Boolean> checklist, TypedArray tarray) {
        super(context, R.layout.listitem, textlist);
        this.context = context;
        this.textlist = textlist;
        this.checklist = checklist;
        this.typedArray = tarray;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder = null;

        prefs = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        edt = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem, parent, false);

            // configure view holder
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.listimage);
            viewHolder.text = (TextView) convertView.findViewById(R.id.listtext);
            viewHolder.check = (CheckBox) convertView.findViewById(R.id.listcheck);
            viewHolder.button = (ImageButton) convertView.findViewById(R.id.listbutton);
            convertView.setTag(viewHolder);
            /*convertView.setTag(R.id.listimage, viewHolder.image);
            convertView.setTag(R.id.listedit, viewHolder.etext);
            convertView.setTag(R.id.listcheck, viewHolder.check);*/

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.check.setChecked(checklist.get(position));
        viewHolder.image.setImageResource(typedArray.getResourceId(position, -1));
        viewHolder.text.setText(textlist.get(position));
        viewHolder.check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                System.out.println("clicked!");
                System.out.println("ctotal!: " + total);
                total=prefs.getInt("total",0);
                if (((CheckBox) v).isChecked()) {
                    if(total<4) {
                        checklist.set(position, true);
                        edt.putBoolean("check" + position, true);
                        edt.commit();
                    }
                    else {
                        viewHolder.check.setChecked(false);
                        checklist.set(position, false);
                        edt.putBoolean("check" + position, false);
                        edt.commit();
                        ((MainActivity)context).listUpdate();
                        Toast.makeText(context, "You may only select up to 4 icons",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    checklist.set(position, false);
                    edt.putBoolean("check" + position, false);
                    edt.commit();
                }
                total = 0;
                for (int i = 0; i < 10; i++) {
                    if (prefs.getBoolean("check" + i, false)) {
                        total = total + 1;
                        edt.putInt("total", total);
                        edt.commit();
                        System.out.println("total!: " + total);
                    }
                }

            ((MainActivity) context).listUpdate();

            }
        });
        viewHolder.button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("clicktest");
                        showChangeLangDialog();
                        edt.putInt("position", position);
                        edt.commit();
                    }
                });



        return convertView;
    }

    public void showChangeLangDialog() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
    dialogBuilder.setView(dialogView);

    final EditText tedt = (EditText) dialogView.findViewById(R.id.edit1);

        if(Platform.isCloverMobile() || Platform.isCloverMini()){
            chars = 60;
        }
        else if(Platform.isCloverStation()){
            chars = 90;
        }

    dialogBuilder.setTitle("Custom dialog");
    dialogBuilder.setMessage("Enter text to display next to icon ("+chars+" character max)");
    dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            int posint = prefs.getInt("position", 0);
            textlist.set(posint, tedt.getText().toString());
            edt.putString("text" + posint, tedt.getText().toString());
            edt.commit();
            if (tedt.getText().toString().length() > chars) {
                Toast.makeText(context, "Your message has exceed "+chars+" characters and may not display correctly",
                        Toast.LENGTH_LONG).show();
            }
                    ((MainActivity) context).listUpdate();
        }
    });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
    AlertDialog b = dialogBuilder.create();
    b.show();

    }
}