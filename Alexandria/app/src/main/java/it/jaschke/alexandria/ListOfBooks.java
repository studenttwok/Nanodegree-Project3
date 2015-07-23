package it.jaschke.alexandria;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.data.AlexandriaContract;


public class ListOfBooks extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = ListOfBooks.class.getSimpleName();

    private BookListAdapter bookListAdapter;
    private ListView bookList;
    private int position = ListView.INVALID_POSITION;
    private EditText searchText;

    private final int LOADER_ID = 10;

    private FrameLayout rightContainer;
    private int selectedBookPosition = -1;

    public ListOfBooks() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        Cursor cursor = getActivity().getContentResolver().query(
                AlexandriaContract.BookEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );


        bookListAdapter = new BookListAdapter(getActivity(), cursor, 0);
        View rootView = inflater.inflate(R.layout.fragment_list_of_books, container, false);
        searchText = (EditText) rootView.findViewById(R.id.searchText);
        rootView.findViewById(R.id.searchButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListOfBooks.this.restartLoader();
                    }
                }
        );

        bookList = (ListView) rootView.findViewById(R.id.listOfBooks);
        bookList.setAdapter(bookListAdapter);

        boolean twoPane = getResources().getBoolean(R.bool.two_pane);
        if (twoPane) {
            //bookList.setItemsCanFocus(false);
            bookList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        } else {
            //bookList.setItemsCanFocus(true);
            bookList.setChoiceMode(ListView.CHOICE_MODE_NONE);
        }

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = bookListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    selectedBookPosition = position;
                    onItemSelected(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
                }
            }
        });

        if (rootView.findViewById(R.id.right_container) != null) {
            rightContainer = (FrameLayout) rootView.findViewById(R.id.right_container);
        }

        return rootView;
    }


    // Call from listofbooks fragment
    public void onItemSelected(String ean) {
        Log.d(LOG_TAG, "onItemSelected");

        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        FragmentManager fm;
        FragmentTransaction ft;



        if (getResources().getBoolean(R.bool.two_pane) == true) {
            // two pane mode
            fm = getChildFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.right_container, fragment);
            ft.commit();
        } else {
            // one pane mode
            fm = getFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.addToBackStack(LOG_TAG);
            ft.commit();
        }

    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final String selection = AlexandriaContract.BookEntry.TITLE +" LIKE ? OR " + AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
        String searchString =searchText.getText().toString();

        if(searchString.length()>0){
            searchString = "%"+searchString+"%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString,searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookListAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            bookList.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.books);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(LOG_TAG, "onViewStateRestored");
        ((MainActivity) getActivity()).setTitle(R.string.books);

        ((MainActivity)getActivity()).restoreActionBar();

        if (savedInstanceState != null) {
            selectedBookPosition = savedInstanceState.getInt("selectedBookPosition");
            Log.d(LOG_TAG, "selectedBookPosition: " + selectedBookPosition);
        }

        // If the use rotate the screen, we should try to select the selected book, when it is in two pane mode...
        if (selectedBookPosition != -1 && getResources().getBoolean(R.bool.two_pane)) {
            //bookList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            bookList.requestFocusFromTouch();
            //bookList.clearChoices();
            bookList.setSelection(selectedBookPosition);

            /*
            Cursor cursor = bookListAdapter.getCursor();
            if (cursor != null && cursor.moveToPosition(selectedBookPosition)) {
                onItemSelected(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
            }
            */

            bookList.performItemClick(bookList.getAdapter().getView(selectedBookPosition, null, null), selectedBookPosition, selectedBookPosition);

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
        Log.d(LOG_TAG, "selectedBookPosition: " + selectedBookPosition);
        outState.putInt("selectedBookPosition", selectedBookPosition);

    }
}
