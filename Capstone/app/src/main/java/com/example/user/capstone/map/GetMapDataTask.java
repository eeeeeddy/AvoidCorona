package com.example.user.capstone.map;


import com.example.user.capstone.helper.L;

/**
 * SubItemRow db를 비동기로 가져오기 위함
 */
public class GetMapDataTask extends BaseAsyncTask<Void, Void, Object> {
    private TaskListener mTaskListener;
    private DataFetcher mFetcher;

    @Override
    protected Object doInBackground(Void... params) {
        Object data = mFetcher.getData();
        return data;
    }

    @Override
    protected void onPostExecute(Object data) {
        if (mTaskListener != null) {
            mTaskListener.onComplete(data);
        }
    }

    public interface TaskListener {
        void onComplete(Object data);
    }

    public interface DataFetcher {
        Object getData();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public static class Builder {

        private DataFetcher mFetcher;
        private TaskListener mCallback;

        public Builder setFetcher(DataFetcher fetcher) {
            mFetcher = fetcher;
            return this;
        }

        public Builder setCallback(TaskListener callback) {
            mCallback = callback;
            return this;
        }

        public GetMapDataTask build() {
            GetMapDataTask cursorTask = new GetMapDataTask();
            cursorTask.mTaskListener = mCallback;
            cursorTask.mFetcher = mFetcher;
            return cursorTask;
        }
    }
}
