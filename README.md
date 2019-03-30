# ScaterKnife

<a href="https://996.icu"><img src="https://img.shields.io/badge/link-996.icu-red.svg"></a>

An IntelliJ plugin for converting Android ButterKnife Zelezny generated @BindView code to Scala lazy val declaration.

#### For generate Scala lazy val in Activity:

1.Generate @BindView code in a Java file;

2.Copy generated code to a Scala file;

3.Import needed packages and do not let IDE convert Java source code to Scala;

4.Ensure nothing is selected;

5.Tools -> ScaterKnife, then Scala lazy val declarations are generated.

```
  lazy val mButton = findViewById(R.id.button).asInstanceOf[Button]
```

#### For generate Scala lazy val in Fragment / ViewHolder:

1-3.Same as the procedure in Activity;

4.Select the variable name of fragment view / itemView;

5.Tools -> ScaterKnife, then Scala lazy val declarations are generated.

```
  lazy val mButton = itemView.findViewById(R.id.button).asInstanceOf[Button]
```
