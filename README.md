# Statesman

The Statesman project (also known as STAGS, or Statesman Text Adventure Game System) is a platform that aims to enable the creation of modern text adventure games. Inspired by early computer games like "Zork", Statesman aims to provide a similar immersive and interactive experience for players.

Interactive fiction or text adventures were very popular in the late-70s until the mid-80s because of games like Colossal Cave Adventure by William Crowther and Zork by Dave Lebling (Rignall, 2015). These games allowed the reader to input phrases into the computer to pass instructions and control their character, with the program returning the result of their instruction. Simple terms, like walk, talk, look, and take, were essential in interacting with the environment, using words as a substitute for WASD or arrow keys or the mouse for many actions.

Drawing inspiration from early text adventure games like "Zork," Statesman seeks to create a modern system for text adventure games using Java. Unlike typical video games that focus solely on entertainment, Statesman emphasizes the "edutainment" aspect, combining entertainment and educational value.

Overall, the Statesman project aims to provide an immersive and interactive text-based adventure game experience, inspire creativity and teamwork, and promote education and problem-solving skills. As Montfort (2013) notes, interactive fiction offers a wide range of genres, including utopias, revenge plays, horrors, parables, intrigues, and codework, with pieces in this form resonating with and reworking works by authors such as Gilgamesh, Shakespeare, Eliot, and Tolkien.

### References
Rignall, J. (2015, December 25). *Dave Lebling on the Genesis of the Adventure Game – and the Creation of Zork*. <http://web.archive.org/web/20160201161307/http://www.usgamer.net/articles/dave-lebling-interview>

Montfort, N. (2013, May 17). *Riddle Machines: The History and Nature of Interactive Fiction*. Wiley Online Library. https://onlinelibrary.wiley.com/doi/10.1002/9781405177504.ch14

## Game Scripts
Statesman features its own custom scripting language and is similar in syntax to Ruby. The documentation (still subject to change) for this scripting language can be found in the [`/docs`](/docs) directory, while sample scripts are available in the [`/src/scripts`](/src/scripts) directory. Script files have the `.gs` extension.

## Samples
- [Pacific Liberty](https://github.com/Lupalop/PacificLiberty): Showcases many of the project's features. The game follows a young man who finds himself transported back in time to the period of the Bataan Death March, and must use his wits to survive the harsh conditions of war.

## Testing
Testing out the project is relatively straightforward.
- Clone the repository and open it in an integrated development environment (IDE) such as Eclipse.
- The program can then be compiled, and the custom scripting language can be explored by modifying the sample scripts.

There is a C# port available in the [`port-csharp`](https://github.com/Lupalop/Statesman/tree/port-csharp) branch.

## License
This project is licensed under the Mozilla Public License 2.0 (MPL 2.0). Please see the [`LICENSE`](/LICENSE) file for more details.

## Authors
The list of authors and contributors to this project can be found in the [`AUTHORS`](/AUTHORS) file in the root directory of this repository.
