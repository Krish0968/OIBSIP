package com.oasis.nexuslibrary.util;

import com.oasis.nexuslibrary.entity.*;
import com.oasis.nexuslibrary.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AuthorRepository authorRepository, CategoryRepository categoryRepository,
                      BookRepository bookRepository, MemberRepository memberRepository,
                      PasswordEncoder passwordEncoder) {
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.count() > 0) {
            // Already seeded
            return;
        }

        // 1. Seed Roles & Accounts
        seedUsers();

        // 2. Seed Authors
        Map<String, Author> authors = seedAuthors();

        // 3. Seed Categories
        Map<String, Category> categories = seedCategories();

        // 4. Seed 50 Books
        seedBooks(authors, categories);
    }

    private void seedUsers() {
        // Seed Admin Account
        Member admin = Member.builder()
                .name("System Administrator")
                .email("admin")
                .password(passwordEncoder.encode("admin123"))
                .phone("+91 9999988888")
                .role(Role.ADMIN)
                .membershipDate(LocalDate.now().minusYears(1))
                .status(MemberStatus.ACTIVE)
                .address("New Delhi Library Headquarters")
                .build();
        memberRepository.save(admin);

        // Seed Member 1: Krish Sharma
        Member member1 = Member.builder()
                .name("Krish Sharma")
                .email("krish@mail.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 9876543210")
                .role(Role.MEMBER)
                .membershipDate(LocalDate.now().minusMonths(6))
                .status(MemberStatus.ACTIVE)
                .address("E-12, Green Park, New Delhi")
                .build();
        memberRepository.save(member1);

        // Seed Member 2: Aarav Mehta
        Member member2 = Member.builder()
                .name("Aarav Mehta")
                .email("aarav@mail.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 9123456789")
                .role(Role.MEMBER)
                .membershipDate(LocalDate.now().minusMonths(3))
                .status(MemberStatus.ACTIVE)
                .address("Block C, Sector 15, Noida")
                .build();
        memberRepository.save(member2);

        // Seed Member 3: Rohan Verma
        Member member3 = Member.builder()
                .name("Rohan Verma")
                .email("rohan@mail.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 8887776665")
                .role(Role.MEMBER)
                .membershipDate(LocalDate.now().minusWeeks(2))
                .status(MemberStatus.ACTIVE)
                .address("Flat 402, Royal Residency, Gurugram")
                .build();
        memberRepository.save(member3);
    }

    private Map<String, Author> seedAuthors() {
        Map<String, Author> map = new HashMap<>();
        String[] names = {
            "Robert C. Martin", "Joshua Bloch", "Martin Fowler", 
            "Frank Herbert", "Isaac Asimov", "George Orwell", 
            "Fyodor Dostoevsky", "J.R.R. Tolkien", "J.K. Rowling", 
            "Agatha Christie", "Arthur Conan Doyle"
        };
        
        String[] bios = {
            "Software engineer, consultant, and author. Widely known as 'Uncle Bob'. Author of Clean Code.",
            "American software engineer and writer, former Distinguished Engineer at Sun Microsystems and Google. Author of Effective Java.",
            "British software developer, author and international speaker on software design. Chief Scientist at ThoughtWorks.",
            "American science fiction writer best known for the 1965 novel Dune and its five sequels.",
            "American writer and professor of biochemistry at Boston University, known for his works of science fiction.",
            "English novelist, essayist, journalist and critic, famous for 1984 and Animal Farm.",
            "Russian novelist, short story writer, essayist and journalist. Author of Crime and Punishment.",
            "English writer, poet, philologist, and academic, best known as the author of The Hobbit and The Lord of the Rings.",
            "British author, philanthropist, film producer, and screenwriter, best known for writing the Harry Potter fantasy series.",
            "English writer known for her 66 detective novels and 14 short story collections, particularly revolving around Hercule Poirot.",
            "British writer and physician, most noted for creating the fictional detective Sherlock Holmes."
        };

        for (int i = 0; i < names.length; i++) {
            Author author = Author.builder()
                    .name(names[i])
                    .biography(bios[i])
                    .build();
            map.put(names[i], authorRepository.save(author));
        }
        return map;
    }

    private Map<String, Category> seedCategories() {
        Map<String, Category> map = new HashMap<>();
        String[] names = {"Technology", "Sci-Fi", "Classics", "Fantasy", "Mystery"};
        String[] descs = {
            "Computer science, coding practices, algorithms, software design, and engineering methodologies.",
            "Speculative fiction exploring futuristic concepts, space travel, time travel, and technological advancements.",
            "Literary masterpieces and timeless historical fiction recognized across generations.",
            "Magical narratives, mythical creatures, epic world-building, and heroic sagas.",
            "Crime solving, detective thrillers, suspenseful deduction, and unresolved secrets."
        };

        for (int i = 0; i < names.length; i++) {
            Category cat = Category.builder()
                    .name(names[i])
                    .description(descs[i])
                    .build();
            map.put(names[i], categoryRepository.save(cat));
        }
        return map;
    }

    private void seedBooks(Map<String, Author> authors, Map<String, Category> categories) {
        
        // Define books data arrays
        // Structure: Title, ISBN, Author Name, Category Name, Publisher, Language, PubYear, TotalCopies, Shelf, Image, Desc
        String[][] bookData = {
            // Technology (10 books)
            {"Clean Code", "9780132350884", "Robert C. Martin", "Technology", "Prentice Hall", "English", "2008", "5", "T-01", "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400", "A handbook of agile software craftsmanship. Teaches you how to write cleaner and more maintainable code."},
            {"The Clean Coder", "9780137081073", "Robert C. Martin", "Technology", "Prentice Hall", "English", "2011", "3", "T-02", "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400", "Rules of professional conduct for software developers. Learn how to estimate, code, test, and collaborate."},
            {"Clean Architecture", "9780134494166", "Robert C. Martin", "Technology", "Prentice Hall", "English", "2017", "4", "T-03", "https://images.unsplash.com/photo-1618401471353-b98aedd07871?w=400", "Presents the universal rules of software architecture and helps developers build flexible frameworks."},
            {"Effective Java", "9780134685991", "Joshua Bloch", "Technology", "Addison-Wesley", "English", "2018", "6", "T-04", "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400", "The definitive guide to Java platform best practices. Essential reading for every Java developer."},
            {"Refactoring", "9780134757599", "Martin Fowler", "Technology", "Addison-Wesley", "English", "2018", "4", "T-05", "https://images.unsplash.com/photo-1531403009284-440f080d1e12?w=400", "Teaches developers how to improve the design of existing code without modifying its external behavior."},
            {"Analysis Patterns", "9780201895421", "Martin Fowler", "Technology", "Addison-Wesley", "English", "1996", "2", "T-06", "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400", "Presents reusable conceptual models for database analysis, reporting, and enterprise design mappings."},
            {"UML Distilled", "9780321193681", "Martin Fowler", "Technology", "Addison-Wesley", "English", "2003", "3", "T-07", "https://images.unsplash.com/photo-1508921912186-1d1a45ebb3c1?w=400", "A brief guide to the standard object modeling language, providing essential UML diagram concepts."},
            {"Clean Craftsmanship", "9780137446544", "Robert C. Martin", "Technology", "Pearson", "English", "2021", "4", "T-08", "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=400", "Explores the disciplines, standards, and ethics of professional software craftsmanship."},
            {"Patterns of Enterprise Application Architecture", "9780321127426", "Martin Fowler", "Technology", "Addison-Wesley", "English", "2002", "5", "T-09", "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=400", "Helps enterprise developers deal with data representation, concurrency, and distribution architectures."},
            {"Java Puzzlers", "9780321336781", "Joshua Bloch", "Technology", "Addison-Wesley", "English", "2005", "2", "T-10", "https://images.unsplash.com/photo-1605379399642-870262d3d051?w=400", "Contains 95 brainteasers about the Java programming language, highlighting corner cases and traps."},
            
            // Sci-Fi (10 books)
            {"Dune", "9780441172719", "Frank Herbert", "Sci-Fi", "Chilton Books", "English", "1965", "8", "S-01", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400", "The epic space opera set on the desert planet Arrakis, focusing on politics, religion, and ecology."},
            {"Dune Messiah", "9780441172696", "Frank Herbert", "Sci-Fi", "Putnam", "English", "1969", "5", "S-02", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400", "The second book in the Dune saga, following the struggles of Paul Atreides as Emperor."},
            {"Children of Dune", "9780441104024", "Frank Herbert", "Sci-Fi", "Putnam", "English", "1976", "4", "S-03", "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400", "The third book in the saga, detailing Paul's children, Leto II and Ghanima, and their imperial destiny."},
            {"Foundation", "9780553293357", "Isaac Asimov", "Sci-Fi", "Gnome Press", "English", "1951", "6", "S-04", "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=400", "Hari Seldon predicts the fall of the Galactic Empire and creates the Foundation to preserve knowledge."},
            {"Foundation and Empire", "9780553293371", "Isaac Asimov", "Sci-Fi", "Gnome Press", "English", "1952", "4", "S-05", "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=400", "Details the clash of the Foundation with the remnants of the Empire, and the threat of the Mule."},
            {"Second Foundation", "9780553293364", "Isaac Asimov", "Sci-Fi", "Gnome Press", "English", "1953", "5", "S-06", "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=400", "Focuses on the search for the secretive Second Foundation, which guides Seldon's plan from the shadows."},
            {"I, Robot", "9780553382563", "Isaac Asimov", "Sci-Fi", "Gnome Press", "English", "1950", "7", "S-07", "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=400", "A collection of science fiction short stories detailing the Three Laws of Robotics and human-machine relations."},
            {"God Emperor of Dune", "9780441294672", "Frank Herbert", "Sci-Fi", "Putnam", "English", "1981", "3", "S-08", "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?w=400", "Leto II, now the sandworm God Emperor, guides humanity along his Golden Path over thousands of years."},
            {"Heretics of Dune", "9780441328000", "Frank Herbert", "Sci-Fi", "Putnam", "English", "1984", "3", "S-09", "https://images.unsplash.com/photo-1444703686981-a3abbc4d4fe3?w=400", "Continues the Dune saga 1500 years after the death of the God Emperor, as new factions arise."},
            {"Chapterhouse: Dune", "9780441102679", "Frank Herbert", "Sci-Fi", "Putnam", "English", "1985", "3", "S-10", "https://images.unsplash.com/photo-1538370965046-79c0d6907d47?w=400", "The final book written by Frank Herbert, showing the Bene Gesserit struggle against the Honored Matres."},
            
            // Classics (10 books)
            {"1984", "9780451524935", "George Orwell", "Classics", "Secker & Warburg", "English", "1949", "10", "C-01", "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400", "The classic dystopian novel exploring totalitarian surveillance under Big Brother and the party."},
            {"Animal Farm", "9780451526342", "George Orwell", "Classics", "Secker & Warburg", "English", "1945", "8", "C-02", "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400", "A satirical allegorical novella about animal rebellion on a farm, mirroring the Russian Revolution."},
            {"Crime and Punishment", "9780140449136", "Fyodor Dostoevsky", "Classics", "The Russian Messenger", "English", "1866", "5", "C-03", "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400", "Raskolnikov commits murder in St. Petersburg and suffers intense psychological and moral guilt."},
            {"The Idiot", "9780140449419", "Fyodor Dostoevsky", "Classics", "The Russian Messenger", "English", "1869", "4", "C-04", "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400", "Depicts the saintly Prince Myshkin entering the volatile, ego-driven high society of Russia."},
            {"The Brothers Karamazov", "9780374528379", "Fyodor Dostoevsky", "Classics", "The Russian Messenger", "English", "1880", "4", "C-05", "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400", "Dostoevsky's final masterpiece dealing with faith, doubt, morality, patricide, and free will."},
            {"Demons", "9780679734222", "Fyodor Dostoevsky", "Classics", "The Russian Messenger", "English", "1872", "3", "C-06", "https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=400", "A political satire depicting the tragic consequences of political and moral nihilism in Russia."},
            {"Homage to Catalonia", "9780156421171", "George Orwell", "Classics", "Secker & Warburg", "English", "1938", "3", "C-07", "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=400", "Orwell's personal account of his fight in the Spanish Civil War against fascist forces."},
            {"Notes from Underground", "9780679734512", "Fyodor Dostoevsky", "Classics", "Epoch", "English", "1864", "4", "C-08", "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400", "Considered the first existentialist novel, presenting the memoirs of a bitter, isolated narrator."},
            {"Down and Out in Paris and London", "9780156262248", "George Orwell", "Classics", "Gollancz", "English", "1933", "3", "C-09", "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=400", "Orwell's memoir documenting his struggles with poverty and working menial jobs in Paris and London."},
            {"The House of the Dead", "9780140444568", "Fyodor Dostoevsky", "Classics", "Vremya", "English", "1862", "2", "C-10", "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400", "A semi-autobiographical novel depicting the harsh life of prisoners in a Siberian labor camp."},

            // Fantasy (10 books)
            {"The Hobbit", "9780345339683", "J.R.R. Tolkien", "Fantasy", "George Allen & Unwin", "English", "1937", "8", "F-01", "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400", "Bilbo Baggins goes on a quest with dwarves and Gandalf to reclaim Lonely Mountain from Smaug."},
            {"The Fellowship of the Ring", "9780345339706", "J.R.R. Tolkien", "Fantasy", "George Allen & Unwin", "English", "1954", "7", "F-02", "https://images.unsplash.com/photo-1608889175123-8ec330b86f84?w=400", "Frodo begins his quest to destroy the One Ring, forming the Fellowship at Rivendell."},
            {"The Two Towers", "9780345339713", "J.R.R. Tolkien", "Fantasy", "George Allen & Unwin", "English", "1954", "6", "F-03", "https://images.unsplash.com/photo-1618336753974-aae8e04506aa?w=400", "Follows the splintered fellowship defending Rohan and Frodo's journey to Mordor with Gollum."},
            {"The Return of the King", "9780345339737", "J.R.R. Tolkien", "Fantasy", "George Allen & Unwin", "English", "1955", "7", "F-04", "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?w=400", "Aragorn claims the throne of Gondor as Frodo and Sam approach Mount Doom for the climax."},
            {"Harry Potter and the Sorcerer's Stone", "9780590353427", "J.K. Rowling", "Fantasy", "Scholastic", "English", "1997", "12", "F-05", "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400", "Harry Potter discovers he is a wizard and begins his first year at Hogwarts School."},
            {"Harry Potter and the Chamber of Secrets", "9780439064873", "J.K. Rowling", "Fantasy", "Scholastic", "English", "1998", "9", "F-06", "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?w=400", "Harry returns to Hogwarts and investigates mysterious petrifications linked to the Slytherin heir."},
            {"Harry Potter and the Prisoner of Azkaban", "9780439136358", "J.K. Rowling", "Fantasy", "Scholastic", "English", "1999", "9", "F-07", "https://images.unsplash.com/photo-1592492159418-09f31330c6bd?w=400", "Harry learns that the escaped mass-murderer Sirius Black is hunting him, discovering dark secrets."},
            {"The Silmarillion", "9780345325815", "J.R.R. Tolkien", "Fantasy", "George Allen & Unwin", "English", "1977", "5", "F-08", "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400", "Tolkien's mythopoeic collection detailing the ancient lore, deities, and creation of Middle-earth."},
            {"Harry Potter and the Goblet of Fire", "9780439139595", "J.K. Rowling", "Fantasy", "Scholastic", "English", "2000", "8", "F-09", "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400", "Harry is selected as a champion in the Triwizard Tournament, leading to a dark trap."},
            {"Unfinished Tales", "9780395299173", "J.R.R. Tolkien", "Fantasy", "George Allen & Unwin", "English", "1980", "3", "F-10", "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=400", "A collection of narratives concerning Middle-earth, edited and published posthumously by Christopher Tolkien."},

            // Mystery (10 books)
            {"The Adventures of Sherlock Holmes", "9780140437713", "Arthur Conan Doyle", "Mystery", "George Newnes", "English", "1892", "8", "M-01", "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400", "A collection of twelve detective stories featuring the brilliant consultant Sherlock Holmes."},
            {"The Hound of the Baskervilles", "9780140437867", "Arthur Conan Doyle", "Mystery", "George Newnes", "English", "1902", "6", "M-02", "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400", "Sherlock Holmes and Dr. Watson investigate a spectral hound terrorizing Dartmoor family heirs."},
            {"The Memoirs of Sherlock Holmes", "9780192123090", "Arthur Conan Doyle", "Mystery", "George Newnes", "English", "1894", "5", "M-03", "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400", "A collection of Holmes detective stories, including the dramatic 'Final Problem' with Moriarty."},
            {"The Return of Sherlock Holmes", "9780747535560", "Arthur Conan Doyle", "Mystery", "George Newnes", "English", "1905", "5", "M-04", "https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=400", "Holmes miraculously returns to London after Reichenbach Falls and resumes solving mysteries with Watson."},
            {"A Study in Scarlet", "9780140439083", "Arthur Conan Doyle", "Mystery", "Ward Lock & Co", "English", "1887", "6", "M-05", "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=400", "The first Sherlock Holmes novel, detailing his meeting with Dr. John Watson and their first case."},
            {"The Sign of Four", "9780140439076", "Arthur Conan Doyle", "Mystery", "Spencer Blackett", "English", "1890", "5", "M-06", "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400", "Holmes and Watson solve a mystery involving stolen treasure, secret pacts, and a betrayal in India."},
            {"The Valley of Fear", "9780140437720", "Arthur Conan Doyle", "Mystery", "Associated Newspapers", "English", "1915", "4", "M-07", "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=400", "Holmes investigates a murder at a manor house, discovering roots linked to a secret society in America."},
            {"His Last Bow", "9780747535607", "Arthur Conan Doyle", "Mystery", "John Murray", "English", "1917", "4", "M-08", "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400", "A collection of later Sherlock Holmes cases, including his counter-espionage work on the eve of WWI."},
            {"The Case-Book of Sherlock Holmes", "9780747535614", "Arthur Conan Doyle", "Mystery", "John Murray", "English", "1927", "4", "M-09", "https://images.unsplash.com/photo-1608889175123-8ec330b86f84?w=400", "The final set of Sherlock Holmes short stories published in the Strand magazine."},
            {"Murder on the Orient Express", "9780007119318", "Agatha Christie", "Mystery", "Collins Crime Club", "English", "1934", "8", "M-10", "https://images.unsplash.com/photo-1618336753974-aae8e04506aa?w=400", "Poirot investigates a murder on a snowbound train, discovering that every passenger is a suspect."}
        };

        for (String[] data : bookData) {
            String title = data[0];
            String isbn = data[1];
            String authorName = data[2];
            String categoryName = data[3];
            String publisher = data[4];
            String language = data[5];
            Integer pubYear = Integer.parseInt(data[6]);
            Integer copies = Integer.parseInt(data[7]);
            String shelf = data[8];
            String desc = data[10];

            Author author = authors.get(authorName);
            Category category = categories.get(categoryName);

            Book book = Book.builder()
                    .title(title)
                    .isbn(isbn)
                    .author(author)
                    .category(category)
                    .publisher(publisher)
                    .language(language)
                    .publicationYear(pubYear)
                    .totalCopies(copies)
                    .availableCopies(copies)
                    .shelfNumber(shelf)
                    .status(BookStatus.AVAILABLE)
                    .description(desc)
                    .build();
            bookRepository.save(book);
        }
    }
}
