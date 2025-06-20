package com.pinont.plugins.Command;

import com.pinont.lib.api.annotation.AutoRegister;
import com.pinont.lib.api.command.SimpleCommand;
import com.pinont.lib.api.items.CustomItem;
import com.pinont.plugins.Toy.Ball;
import com.pinont.plugins.Toy.BouncingArrow;
import com.pinont.plugins.Toy.IceBomb;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@AutoRegister
public class Toy implements SimpleCommand {

    // issue with auto register item so use manual registration for a while
    private final List<CustomItem> toys = List.of(new Ball(), new IceBomb(), new BouncingArrow());

    public List<String> getToyNames() {
        if (toys.isEmpty()) {
            return List.of("");
        }
        return toys.stream().map(item -> item.getName().replace(" ", "_")).toList();
    }

    @Override
    public String getName() {
        return "toy";
    }

    @Override
    public String usage(Boolean bool) {
        return "/toy get <toy_name> [<amount>]";
    }

    @Override
    public String description() {
        return "Get Toys";
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        if (strings.length < 2) {
            commandSourceStack.getSender().sendMessage("Usage: " + usage(true));
            return;
        }
        int amount = 1; // Default amount
        Player player = (Player) commandSourceStack.getSender();
        String toyName = strings[1].toLowerCase();

        CustomItem toy = toys.stream().filter(itm -> itm.getName().equalsIgnoreCase(toyName) || itm.getName().equalsIgnoreCase(toyName.replace("_", " "))).findFirst().orElse(null);

        if (strings.length > 2) {
            try {
                amount = Integer.parseInt(strings[2]);
                if (amount <= 0) {
                    player.sendMessage("Amount must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount: " + strings[2]);
            }
        }
        if (toy != null) {
            player.getInventory().addItem(toy.register().setAmount(amount).addInteraction(toy.getInteraction()).create());
            player.sendMessage("You have received a " + toy.getName() + "!");
        } else {
            player.sendMessage("Unknown toy: " + toyName);
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return switch (args.length) {
            case 0, 1 -> List.of("get");
            case 2 -> getToyNames();
            default -> List.of();
        };
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender instanceof Player && sender.hasPermission("singularity.toy.use");
    }
}
