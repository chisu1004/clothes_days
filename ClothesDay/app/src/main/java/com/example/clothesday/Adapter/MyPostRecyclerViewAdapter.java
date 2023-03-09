package com.example.clothesday.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.R;
import com.example.clothesday.data.Indicator;
import com.example.clothesday.data.Time;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.fragment.PostFragment;
import com.example.clothesday.fragment.PostManagementFragment;
import com.example.clothesday.request.PostDeleteRequest;
import com.example.clothesday.request.ScrapRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<MyPostRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<PostDTO> mData = new ArrayList<PostDTO>();
    Activity activity;
    FragmentManager fm;
    int pos;



    public MyPostRecyclerViewAdapter(Context context, ArrayList<PostDTO> PostDTO, Activity activity, FragmentManager fm)  {
        this.context = context;
        this.mData = PostDTO;
        this.activity = activity;
        this.fm = fm;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_two_line, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Indicator indicator = new Indicator();
        ArrayList<Uri> uriList = new ArrayList<>();
        PostDTO post = mData.get(position);

        StringTokenizer stk;
        int image_count = 0;
        //뷰페이저
        if (post.getPO_PIC() != null) {
            stk = new StringTokenizer(post.getPO_PIC(), ",");
            image_count = stk.countTokens();


            // 이미지 url을 얻어서 리스트에 넣기
            while(stk.hasMoreTokens()){
                uriList.add(Uri.parse("http://dlsxjsptb.cafe24.com/post/image/" +stk.nextToken()));
            }
        }
        ImageSliderAdapter imageslideradapter = new ImageSliderAdapter(context,uriList, activity);
        holder.viewPager2.setOffscreenPageLimit(1);
        holder.viewPager2.setAdapter(imageslideradapter);
        holder.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                indicator.setCurrentIndicator(position, holder.layoutIndicator, context);
            }
        });
        indicator.setupIndicators(image_count, holder.layoutIndicator, context);

        //날짜
        try {
            Time time = new Time();
            String reg = post.getPO_REG_DA() + " " + time.getDayOfWeek(post.getPO_REG_DA());
            holder.date.setText(reg + "요일");
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.date.setTag(position);

    }

    @Override
    public int getItemCount() { return mData.size(); }

    public void DeletePost(int pos) {
        mData.remove(pos);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {

        TextView date, tag;
        ViewPager2 viewPager2;
        LinearLayout layoutIndicator;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager2 = itemView.findViewById(R.id.recyclerview_two_line_viewpager2);
            date = itemView.findViewById(R.id.recyclerview_two_line_date);
            tag = itemView.findViewById(R.id.recyclerview_two_line_tag);
            layoutIndicator = itemView.findViewById(R.id.recyclerview_two_line_indicator);

            date.setOnClickListener(this::onClick);
            viewPager2.setOnClickListener(this::onClick);
            date.setOnCreateContextMenuListener(this);
            viewPager2.setOnCreateContextMenuListener(this);
            date.setOnLongClickListener(this);
            viewPager2.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PostFragment postFragment = new PostFragment();
            int pos = getBindingAdapterPosition();
            PostDTO PostDTO = mData.get(pos);
            Bundle result = new Bundle();
            switch (v.getId()) {
                case R.id.recyclerview_two_line_date:
                    result.putInt("PO_ID", PostDTO.getPO_ID());
                    fm.setFragmentResult("requestKey", result);
                    postFragment.setArguments(result);
                    fm.beginTransaction().detach(postFragment).attach(postFragment).replace(R.id.container, postFragment).addToBackStack(null).commit();
                    break;
                case R.id.recyclerview_two_line_viewpager2:
                    result.putInt("PO_ID", PostDTO.getPO_ID());
                    fm.setFragmentResult("requestKey", result);
                    postFragment.setArguments(result);
                    fm.beginTransaction().detach(postFragment).attach(postFragment).replace(R.id.container, postFragment).addToBackStack(null).commit();
                    break;
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(Menu.NONE, R.id.post_menu_update, 1, "게시글 수정");
            MenuItem Delete = menu.add(Menu.NONE, R.id.post_menu_delete, 2, "게시글 삭제");
            Edit.setOnMenuItemClickListener(onMenuItemClickListener);
            Delete.setOnMenuItemClickListener(onMenuItemClickListener);


        }

        @Override
        public boolean onLongClick(View v) {
            pos = getBindingAdapterPosition();
            date.setTag(pos);
            return false;
        }

        private final MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override  public boolean onMenuItemClick(MenuItem item) {

                PostManagementFragment postManagementFragment = new PostManagementFragment();
                int pos = getBindingAdapterPosition();
                PostDTO PostDTO = mData.get(pos);
                Bundle result = new Bundle();
                switch (item.getItemId()) {
                    case R.id.post_menu_update:
                        result.putInt("PO_ID", PostDTO.getPO_ID());
                        result.putString("type", "update");
                        fm.setFragmentResult("requestKey", result);
                        postManagementFragment .setArguments(result);
                        fm.beginTransaction().detach(postManagementFragment ).attach(postManagementFragment ).replace(R.id.container, postManagementFragment ).addToBackStack(null).commit();
                        return false;
                    case R.id.post_menu_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("게시글 삭제").setMessage("게시글을 삭제하시겠습니까?");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                deletePost(mData.get(pos).getPO_ID(), pos);
                            }
                        });

                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        return false;
                }
                return false;
            }
        };

    } // 뷰홀더 끝

    private void deletePost(int PO_ID, int pos) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resultResponse = response;
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    Boolean res = result.getBoolean("result");
                    System.out.println(pos);

                    if (res){
                        Toast.makeText(activity.getApplicationContext(), "게시글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();

                        DeletePost(pos);
                    } else  {
                        Toast.makeText(activity.getApplicationContext(), "게시글 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }

                    notifyItemChanged(pos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        };
        PostDeleteRequest PostDeleteRequest = new PostDeleteRequest(PO_ID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add( PostDeleteRequest );

    }


}
