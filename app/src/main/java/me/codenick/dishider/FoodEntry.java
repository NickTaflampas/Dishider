package me.codenick.dishider;

public class FoodEntry {

    private int id;
    private String name;
    private String description;

    private int fruitScore;
    private int vegetableScore;
    private int proteinScore;
    private int sugarScore;
    private int carbScore;
    private int fatScore;

    private boolean isSnack;
    private boolean isVegan;

    private FoodEntry(FoodEntryBuilder builder)
    {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        fruitScore = builder.fruitScore;
        vegetableScore = builder.vegetableScore;
        proteinScore = builder.proteinScore;
        sugarScore = builder.sugarScore;
        carbScore = builder.carbScore;
        fatScore = builder.fatScore;

        isSnack = builder.isSnack;
        isVegan = builder.isVegan;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getFruitScore() {
        return fruitScore;
    }

    public int getVegetableScore() {
        return vegetableScore;
    }

    public int getProteinScore() {
        return proteinScore;
    }

    public int getSugarScore() {
        return sugarScore;
    }

    public int getCarbScore() {
        return carbScore;
    }

    public int getFatScore() {
        return fatScore;
    }

    public boolean isSnack() {
        return isSnack;
    }

    public boolean isVegan() {
        return isVegan;
    }

    public static FoodEntryBuilder builder(int id)
    {
        return new FoodEntryBuilder(id);
    }

    public static class FoodEntryBuilder
    {
        protected int id;
        protected String name;
        protected String description;

        protected int fruitScore;
        protected int vegetableScore;
        protected int proteinScore;
        protected int sugarScore;
        protected int carbScore;
        protected int fatScore;

        protected boolean isSnack;
        protected boolean isVegan;

        private FoodEntryBuilder(int id)
        {
            this.id = id;
        }

        public FoodEntryBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public FoodEntryBuilder withDescription(String desc)
        {
            this.description = desc;
            return this;
        }

        public FoodEntryBuilder withFruitScore(int score)
        {
            this.fruitScore = score;
            return this;
        }

        public FoodEntryBuilder withVegetableScore(int score)
        {
            this.vegetableScore = score;
            return this;
        }
        public FoodEntryBuilder withProteinScore(int score)
        {
            this.proteinScore = score;
            return this;
        }
        public FoodEntryBuilder withSugarScore(int score)
        {
            this.sugarScore = score;
            return this;
        }
        public FoodEntryBuilder withCarbScore(int score)
        {
            this.carbScore = score;
            return this;
        }
        public FoodEntryBuilder withFatScore(int score)
        {
            this.fatScore = score;
            return this;
        }

        public FoodEntryBuilder isSnack(boolean isSnack)
        {
            this.isSnack = isSnack;
            return this;
        }

        public FoodEntryBuilder isVegan(boolean isVegan)
        {
            this.isVegan = isVegan;
            return this;
        }

        public FoodEntry build()
        {
            return new FoodEntry(this);
        }

    }


}
