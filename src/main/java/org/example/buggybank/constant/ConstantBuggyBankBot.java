package org.example.buggybank.constant;

public class ConstantBuggyBankBot {

    public final static String COMMAND_START = "/start";
    public final static String COMMAND_MY_DATA = "/mydata";
    public final static String COMMAND_DELETE_DATA = "/deletedata";
    public final static String COMMAND_HELP = "/help";
    public final static String COMMAND_SETTINGS = "/settings";

    public final static String DESCRIPTION_START = "Запуск бота";
    public final static String DESCRIPTION_MY_DATA = "Посмотреть данные пользователя";
    public final static String DESCRIPTION_DELETE_DATA = "Удалить данные пользователя";
    public final static String DESCRIPTION_HELP = "Подробнее о командах";
    public final static String DESCRIPTION_SETTINGS = "Настройки бота";

    public final static String HELP_TEXT = """
            Полный развернутый список команд:

            /start - позволяет запустить бота, поприветствовать Вас и зарегистрировать, если вы присоеденились к нам впервые :)
            /mydata - позволяет просмотреть данные о Вас, бережно хранимые на наших серверах.
            /deletedata - боитесь за свою конфинденциалность? Эта кнопка удалит все данные связанные с вами!
            /help - теряетесь в догадках, что елать с ботом? Ознакомься с его полным функционалом тут.
            /settings - что-то на мдром и заумном, можно отключить фичи
            """;

    public final static String GREETINGS = """
            %s! Приветствую тебя в боте, который поможет тебе не потеряться в твоем малом бизнесте, расти над собой, вместе с твоими доходами.
            
            p.s. Только тут хранятся твои маленькие грязные денежные секретики >:)
            """;
}
