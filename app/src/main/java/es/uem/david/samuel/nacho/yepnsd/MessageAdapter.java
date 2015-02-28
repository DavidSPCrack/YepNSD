package es.uem.david.samuel.nacho.yepnsd;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by usuario.apellido on 28/02/2015.
 *
 * @author david.sancho
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    private Context mContext;
    private List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);

            holder = new ViewHolder();
            holder.setIconImageView((ImageView) convertView.findViewById(R.id.messageIcon));
            holder.setNameLabelView((TextView) convertView.findViewById(R.id.senderLabel));
            holder.setTimeLabelView((TextView) convertView.findViewById(R.id.timeLabel));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ParseObject message = mMessages.get(position);

        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(
                createdAt.getTime(),
                now,
                DateUtils.SECOND_IN_MILLIS).toString();
        holder.setTime(convertedDate);

        String fileType = message.getString(Constantes.ParseClasses.Messages.KEY_FILE_TYPE);
        if(fileType.equals(Constantes.FileTypes.IMAGE)) {
            holder.setIconSource(R.drawable.ic_action_picture);
        } else {
            holder.setIconSource(R.drawable.ic_action_play_over_video);
        }
        holder.setSender(message.getString(Constantes.ParseClasses.Messages.KEY_SENDER_NAME));


        return convertView;

    }

    private static class ViewHolder {
        private ImageView iconImageView;
        private TextView nameLabel;
        private TextView timeLabel;

        private ViewHolder() {
        }

        private void setIconImageView(ImageView iconImageView) {
            this.iconImageView = iconImageView;
        }

        private void setNameLabelView(TextView nameLabel) {
            this.nameLabel = nameLabel;
        }

        public void setTimeLabelView(TextView timeLabel) {
            this.timeLabel = timeLabel;
        }

        private void setIconSource(int id) {
            this.iconImageView.setImageResource(id);
        }

        private void setSender(String sender) {
            this.nameLabel.setText(sender);
        }

        private void setTime(String time) {
            this.timeLabel.setText(time);
        }
    }
}
