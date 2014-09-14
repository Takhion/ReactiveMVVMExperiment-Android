package me.eugeniomarletti.reactivemvvmexperiment.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import me.eugeniomarletti.reactiveandroid.Observable_Android;
import me.eugeniomarletti.reactiveandroid.collection.ObservableList;
import me.eugeniomarletti.reactiveandroid.collection.ReactiveAdapter;
import me.eugeniomarletti.reactiveandroid.property.Property;
import me.eugeniomarletti.reactivemvvmexperiment.R;
import me.eugeniomarletti.reactivemvvmexperiment.model.Person;
import me.eugeniomarletti.reactivemvvmexperiment.viewmodel.ListViewModel;
import org.jetbrains.annotations.NotNull;
import rx.Subscription;
import rx.functions.Action1;

import java.util.HashSet;
import java.util.Set;

public class ListActivity extends Activity
{
    protected final ListViewModel viewModel = new ListViewModel();

    protected @InjectView(R.id.list) ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.inject(this);

        setupBindings();
    }

    protected void setupBindings()
    {
        // number of people -> actionbar title
        displayNumberOfPeople(getNumberOfPeople()); // first time
        Observable_Android.androidSafe(viewModel.people().observe(), this)
                   .subscribe($ -> displayNumberOfPeople(getNumberOfPeople()));

        // people in list
        list.setAdapter(new ListAdapter(this, viewModel.people()));

        //list.postDelayed(
        //        () ->
        //        {
        //            if (viewModel.people().get().size() > 0)
        //            {
        //                viewModel.people().remove(viewModel.people().get().get(0));
        //            }
        //        }, 3000);
    }

    protected int getNumberOfPeople()
    {
        return viewModel.people().get().size();
    }

    protected void displayNumberOfPeople(int n)
    {
        final ActionBar ab = getActionBar();
        if (ab != null)
        {
            ab.setTitle(getResources().getQuantityString(R.plurals.number_of_people, n, n));
        }
    }

    protected static class ListAdapter extends ReactiveAdapter<Person>
    {
        protected final LayoutInflater inflater;
        protected final Activity       activity;

        public ListAdapter(@NotNull Activity activity, @NotNull ObservableList<Person> list)
        {
            super(activity, list);
            this.activity = activity;
            inflater = LayoutInflater.from(activity);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;
            if (convertView != null)
            {
                holder = (ViewHolder)convertView.getTag();
                holder.clearSubscriptions();
            }
            else
            {
                convertView = inflater.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();
                ButterKnife.inject(holder, convertView);
                convertView.setTag(holder);
            }
            final Person p = _getItem(position);

            subscribe(holder, p.name(), name -> holder.name.setText(name));
            subscribe(holder, p.age(), age -> holder.age.setText(age.toString()));

            return convertView;
        }

        protected <T> void subscribe(ViewHolder holder, Property<T> property, Action1<T> onNext)
        {
            holder.subscriptions.add(Observable_Android.androidSafe(property.observe(), activity).subscribe(onNext));
        }

        protected static class ViewHolder
        {
            public @InjectView(R.id.name) TextView name;
            public @InjectView(R.id.age)  TextView age;
            public final Set<Subscription> subscriptions = new HashSet<>();

            public void clearSubscriptions()
            {
                for (Subscription s : subscriptions)
                {
                    s.unsubscribe();
                }
                subscriptions.clear();
            }
        }
    }
}
