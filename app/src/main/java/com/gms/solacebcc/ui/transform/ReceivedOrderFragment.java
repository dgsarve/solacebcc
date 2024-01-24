package com.gms.solacebcc.ui.transform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.gms.solacebcc.R;
import com.gms.solacebcc.databinding.FragmentTransformBinding;
import com.gms.solacebcc.databinding.ItemTransformBinding;
import com.gms.solacebcc.solace.SolaceClient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
public class ReceivedOrderFragment extends Fragment {

    private FragmentTransformBinding binding;
    private SolaceClient solaceClient = new SolaceClient();
    private MqttClient mqttClient = solaceClient.getMqttSolaceClient();
    final CountDownLatch latch = new CountDownLatch(1);
    ListAdapter<String, TransformViewHolder> adapter = new TransformAdapter();

    private ReceivedOrderViewModel receivedOrderViewModel = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         receivedOrderViewModel =
                new ViewModelProvider(this).get(ReceivedOrderViewModel.class);

        binding = FragmentTransformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView recyclerView = binding.recyclerviewTransform;

        recyclerView.setAdapter(adapter);
        receivedOrderViewModel.getTexts().observe(getViewLifecycleOwner(), adapter::submitList);
        // Subscribe to the MQTT topic
        subscribeToMqttTopic();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class TransformAdapter extends ListAdapter<String, TransformViewHolder> {

        private final List<Integer> drawables = Arrays.asList(
                R.drawable.avatar_1,
                R.drawable.avatar_2


        );

        protected TransformAdapter() {
            super(new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }
            });
        }

        @NonNull
        @Override
        public TransformViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTransformBinding binding = ItemTransformBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new TransformViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TransformViewHolder holder, int position) {
            holder.textView.setText(getItem(position));
            holder.imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(holder.imageView.getResources(),
                            drawables.get(position),
                            null));
        }
    }

    private static class TransformViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;

        public TransformViewHolder(ItemTransformBinding binding) {
            super(binding.getRoot());
            imageView = binding.imageViewItemTransform;
            textView = binding.textViewItemTransform;
        }
    }

    private void subscribeToMqttTopic() {
        MqttClient mqttClient = solaceClient.getMqttSolaceClient();
        try {
            mqttClient.subscribe("ecom/orderplaced", 0);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        mqttClient.setCallback(new MqttCallback() {

                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String time = new Timestamp(System.currentTimeMillis()).toString();
                        System.out.println("\nReceived a Message!" +
                                "\n\tTime:    " + time +
                                "\n\tTopic:   " + topic +
                                "\n\tMessage: " + new String(message.getPayload()) +
                                "\n\tQoS:     " + message.getQos() + "\n");
                        // Get the current list of texts from the ViewModel
                        List<String> newTexts = new ArrayList<>(receivedOrderViewModel.getTexts().getValue());
                        newTexts.add(0, "Received: " + new String(message.getPayload()));

                        // Update the ViewModel with the new list
                        receivedOrderViewModel.updateTexts(newTexts);
                        latch.countDown(); // unblock main thread
                    }

                    public void connectionLost(Throwable cause) {
                        System.out.println("Connection to Solace broker lost!" + cause.getMessage());
                        latch.countDown();
                    }

                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("\nMessage was successfully delivered to Solace\n");
                        latch.countDown(); // unblock main thread
                    }
                });
    }

}