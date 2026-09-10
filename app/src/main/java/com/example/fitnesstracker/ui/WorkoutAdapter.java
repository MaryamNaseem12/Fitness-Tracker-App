package com.example.fitnesstracker.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstracker.R;
import com.example.fitnesstracker.data.WorkoutLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    public interface OnWorkoutClickListener {
        void onDeleteClick(WorkoutLog workout);
    }

    private final List<WorkoutLog> workouts = new ArrayList<>();
    private final OnWorkoutClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());

    public WorkoutAdapter(OnWorkoutClickListener listener) {
        this.listener = listener;
    }

    public void setWorkouts(List<WorkoutLog> newWorkouts) {
        this.workouts.clear();
        if (newWorkouts != null) {
            this.workouts.addAll(newWorkouts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_log, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutLog workout = workouts.get(position);

        holder.tvTitle.setText(workout.getWorkoutType());
        holder.tvTimestamp.setText(dateFormat.format(new Date(workout.getTimestamp())));
        holder.tvCalories.setText(workout.getCaloriesBurned() + " kcal");
        holder.tvDuration.setText(workout.getDurationMinutes() + " mins");

        if (workout.getStepsTaken() > 0) {
            holder.tvStepsBullet.setVisibility(View.VISIBLE);
            holder.tvSteps.setVisibility(View.VISIBLE);
            holder.tvSteps.setText(workout.getStepsTaken() + " steps");
        } else {
            holder.tvStepsBullet.setVisibility(View.GONE);
            holder.tvSteps.setVisibility(View.GONE);
        }

        // Icon based on type
        String type = workout.getWorkoutType() != null ? workout.getWorkoutType().toLowerCase() : "";
        if (type.contains("run") || type.contains("walk")) {
            holder.imgIcon.setImageResource(R.drawable.ic_steps);
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_workout_fire);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(workout);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon, btnDelete;
        TextView tvTitle, tvTimestamp, tvCalories, tvDuration, tvSteps, tvStepsBullet;

        WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_workout_icon);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvTitle = itemView.findViewById(R.id.tv_workout_title);
            tvTimestamp = itemView.findViewById(R.id.tv_workout_timestamp);
            tvCalories = itemView.findViewById(R.id.tv_workout_calories);
            tvDuration = itemView.findViewById(R.id.tv_workout_duration);
            tvSteps = itemView.findViewById(R.id.tv_workout_steps);
            tvStepsBullet = itemView.findViewById(R.id.tv_workout_steps_bullet);
        }
    }
}
